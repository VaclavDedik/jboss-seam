/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.action;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Renderer;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.security.AuthorizationException;
import org.jboss.seam.security.Identity;
import org.jboss.seam.wiki.core.action.prefs.UserManagementPreferences;
import org.jboss.seam.wiki.core.action.prefs.WikiPreferences;
import org.jboss.seam.wiki.core.dao.UserDAO;
import org.jboss.seam.wiki.core.model.Role;
import org.jboss.seam.wiki.core.model.User;
import org.jboss.seam.wiki.core.model.WikiUploadImage;
import org.jboss.seam.wiki.core.upload.Uploader;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;
import org.jboss.seam.wiki.preferences.Preferences;
import org.jboss.seam.wiki.preferences.metamodel.PreferenceEntity;
import org.jboss.seam.wiki.util.Hash;
import org.jboss.seam.wiki.util.WikiUtil;

import javax.faces.application.FacesMessage;
import static javax.faces.application.FacesMessage.SEVERITY_INFO;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Name("userHome")
@Scope(ScopeType.CONVERSATION)
public class UserHome extends EntityHome<User> {

    public Long getUserId() {
        return (Long)getId();
    }

    public void setUserId(Long userId) {
        setId(userId);
    }

    @In
    private FacesMessages facesMessages;

    @In
    private UserDAO userDAO;

    @In
    private Hash hashUtil;

    @In(create = true)
    private Renderer renderer;

    @In("#{preferences.get('UserManagement')}")
    UserManagementPreferences prefs;

    private String oldUsername;
    private String password;
    private String passwordControl;
    private List<Role> roles;
    private org.jboss.seam.wiki.core.model.Role defaultRole;
    private Uploader uploader;

    public Uploader getUploader() {
        return uploader;
    }

    public void setUploader(Uploader uploader) {
        this.uploader = uploader;
    }

    public void init() {
        if (isManaged()) {
            if (!Identity.instance().hasPermission("User", "edit", getInstance()) ) {
                throw new AuthorizationException("You don't have permission for this operation");
            }
            if (roles == null) roles = getInstance().getRoles();
            if (oldUsername == null) oldUsername = getInstance().getUsername();

            uploader = (Uploader)Component.getInstance("uploader");

        } else {
            if (!prefs.getEnableRegistration() &&
                !Identity.instance().hasPermission("User", "isAdmin", Component.getInstance("currentUser"))) {
                throw new AuthorizationException("User registration is disabled");
            }

            if (defaultRole == null) defaultRole = (Role)Component.getInstance("newUserDefaultRole");
        }
    }

    public String persist() {

        // Validate
        if (!isUniqueUsername() ||
            !passwordAndControlNotNull() ||
            !passwordMatchesRegex() ||
            !passwordMatchesControl()) {

            // Force re-entry
            setPassword(null);
            setPasswordControl(null);

            return null;
        }

        // Assign default role
        getInstance().getRoles().add(defaultRole);

        // Set password hash
        getInstance().setPasswordHash(hashUtil.hash(getPassword()));

        if (Identity.instance().hasPermission("User", "isAdmin", Component.getInstance("currentUser"))) {
            // Current user is admin and creating a new account, the new account is active automatically
            getInstance().setActivated(true);
            return super.persist();
        } else {

            // Set activation code (unique user in time)
            String seed = getInstance().getUsername() + System.currentTimeMillis() + prefs.getActivationCodeSalt();
            getInstance().setActivationCode( ((Hash)Component.getInstance("hashUtil")).hash(seed) );

            String outcome = super.persist();
            if (outcome != null) {

                try {

                    // Send confirmation email
                    renderer.render("/themes/"
                            + ((WikiPreferences) Preferences.getInstance("Wiki")).getThemeName()
                            + "/mailtemplates/confirmationRegistration.xhtml");

                    /* For debugging
                    facesMessages.addFromResourceBundleOrDefault(
                        FacesMessage.SEVERITY_INFO,
                        getMessageKeyPrefix() + "confirmationEmailSent",
                        "Activiate account: /confirmRegistration.seam?activationCode=" + getInstance().getActivationCode());
                    */

                } catch (Exception ex) {
                    facesMessages.add(FacesMessage.SEVERITY_ERROR, "Couldn't send confirmation email: " + ex.getMessage());
                    return "error";
                }
            }
            return outcome;
        }
    }

    @Restrict("#{s:hasPermission('User', 'edit', userHome.instance)}")
    public String update() {

        if (uploader.hasData()) {
            uploader.uploadNewInstance();
            if (WikiUploadImage.class.isAssignableFrom(uploader.getUpload().getClass())) {
                WikiUploadImage portrait = (WikiUploadImage)uploader.getUpload();
                getLog().debug("updating portrait file data/type");
                getInstance().getProfile().setImageContentType(portrait.getContentType());
                getInstance().getProfile().setImage(
                    WikiUtil.resizeImage(portrait.getData(), portrait.getContentType(), 80) // TODO: Make size configurable?
                );

            } else {
                facesMessages.addFromResourceBundleOrDefault(
                    FacesMessage.SEVERITY_ERROR,
                    "lacewiki.msg.userHome.WrongPortraitImageType",
                    "The file type '{0}' is not supported, the portrait was not updated.",
                    uploader.getUpload().getContentType()
                );
            }
        }
        uploader.reset();

        User adminUser = (User)Component.getInstance("adminUser");
        User guestUser = (User)Component.getInstance("guestUser");

        if ( !getInstance().getId().equals(adminUser.getId()) &&
             !getInstance().getId().equals(guestUser.getId()) &&
             roles != null && roles.size() > 0) {
            // Roles
            getInstance().setRoles(new ArrayList<Role>()); // Clear out the collection
            getInstance().getRoles().addAll(roles);
        }

        // Preferences
        if (preferenceEditor != null) {
            String editorFailed = preferenceEditor.save();
            if (editorFailed != null) return null;
        }

        boolean loginCredentialsModified = false;

        // User wants to change his password
        if (getPassword() != null && getPassword().length() != 0) {
            if (!passwordAndControlNotNull() ||
                !passwordMatchesRegex() ||
                !passwordMatchesControl()) {

                // Force re-entry
                setPassword(null);
                setPasswordControl(null);

                return null;
            } else {
                // Set password hash
                getInstance().setPasswordHash(hashUtil.hash(getPassword()));
                loginCredentialsModified = true;
            }
        }

        // User changed his username
        if (!getInstance().getUsername().equals(oldUsername)) {
            loginCredentialsModified = true;

            // Validate
            if (!isUniqueUsername()) return null;
        }

        if (Identity.instance().hasPermission("User", "isAdmin", Component.getInstance("currentUser"))) {
            // Current user is admin and activated an account
            if (getInstance().isActivated()) {
                getInstance().setActivationCode(null);
            }
        }

        String outcome = super.update();
        if (outcome != null) {

            User currentUser = (User)Component.getInstance("currentUser");
            if (getInstance().getId().equals(currentUser.getId())) {
                // Updated profile of currently logged-in user
                Contexts.getSessionContext().set("currentUser", getInstance());
                
                // TODO: If identity.logout() wouldn't kill my session, I could call it here...
                // And I don't have cleartext password in all cases, so I can't relogin the user automatically
                if (loginCredentialsModified) {
                    Identity.instance().logout();
                    return "updatedCurrentCredentials";
                }
            }
        }

        return outcome;
    }

    @Restrict("#{s:hasPermission('User', 'delete', userHome.instance)}")
    public String remove() {

        // All nodes created by this user are reset to be created by the admin user
        userDAO.resetNodeCreatorToAdmin(getInstance());

        return super.remove();
    }

    @Restrict("#{s:hasPermission('User', 'edit', userHome.instance)}")
    public void removePortrait() {
        getInstance().getProfile().setImage(null);
        getInstance().getProfile().setImageContentType(null);

        facesMessages.addFromResourceBundleOrDefault(
            FacesMessage.SEVERITY_INFO,
            "lacewiki.msg.userHome.PortraitRemoved",
            "The portrait has been removed, save to make changes permanent."
        );
    }


    /* -------------------------- Messages ------------------------------ */

    protected void createdMessage() {
        getFacesMessages().addFromResourceBundleOrDefault(
                SEVERITY_INFO,
                "lacewiki.msg.User.Persist",
                "User account '{0}' has been saved.",
                getInstance().getUsername()
        );
    }

    protected void updatedMessage() {
        getFacesMessages().addFromResourceBundleOrDefault(
                SEVERITY_INFO,
                "lacewiki.msg.User.Update",
                "User account '{0}' has been updated.",
                getInstance().getUsername()
        );
    }

    protected void deletedMessage() {
        getFacesMessages().addFromResourceBundleOrDefault(
                SEVERITY_INFO,
                "lacewiki.msg.User.Delete",
                "User account '{0}' has been deleted.",
                getInstance().getUsername()
        );
    }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPasswordControl() { return passwordControl; }
    public void setPasswordControl(String passwordControl) { this.passwordControl = passwordControl; }

    public List<Role> getRoles() { return roles; }
    @Restrict("#{s:hasPermission('User', 'editRoles', currentUser)}")
    public void setRoles(List<Role> roles) { this.roles = roles; }

    @Restrict("#{s:hasPermission('User', 'isAdmin', currentUser)}")
    public void createHomeDirectory() {

        Authenticator auth = (Authenticator)Component.getInstance("authenticator");
        auth.createHomeDirectory(getInstance());

        facesMessages.addFromResourceBundleOrDefault(
            FacesMessage.SEVERITY_INFO,
            "lacewiki.msg.HomeDirectoryCreated",
            "New home directory has been queued, save settings to commit."
        );
    }

    // Validation rules for persist(), update(), and remove();

    public boolean passwordAndControlNotNull() {
        if (getPassword() == null || getPassword().length() == 0 ||
            getPasswordControl() == null || getPasswordControl().length() == 0) {
            facesMessages.addToControlFromResourceBundleOrDefault(
                "passwordControl",
                FacesMessage.SEVERITY_ERROR,
                "lacewiki.msg.PasswordOrPasswordControlEmpty",
                "Please enter your password twice!"
            );
            return false;
        }
        return true;
    }

    public boolean passwordMatchesRegex() {
        Matcher matcher = Pattern.compile(prefs.getPasswordRegex()).matcher(getPassword());
        if (!matcher.find()) {
            facesMessages.addToControlFromResourceBundleOrDefault(
                "password",
                FacesMessage.SEVERITY_ERROR,
                "lacewiki.msg.PasswordDoesntMatchPattern",
                "Password does not match the pattern: {0}",
                prefs.getPasswordRegex()
            );
            return false;
        }
        return true;
    }

    public boolean passwordMatchesControl() {
        if (!password.equals(passwordControl) ) {
            facesMessages.addToControlFromResourceBundleOrDefault(
                "passwordControl",
                FacesMessage.SEVERITY_ERROR,
                "lacewiki.msg.PasswordControlNoMatch",
                "The passwords don't match."
            );
            return false;
        }
        return true;
    }

    public boolean isUniqueUsername() {
        User foundUser = userDAO.findUser(getInstance().getUsername(), false, false);
        if ( foundUser != null && foundUser != getInstance() ) {
            facesMessages.addToControlFromResourceBundleOrDefault(
                "username",
                FacesMessage.SEVERITY_ERROR,
                "lacewiki.msg.UsernameExists",
                "A user with that name already exists."
            );
            return false;
        }
        return true;
    }

    public void validateUsername() {
        isUniqueUsername();
    }

    public void validatePassword() {
        if (getPassword() != null && getPassword().length() > 0)
            passwordMatchesRegex();
    }

    public void validatePasswordControl() {
        passwordMatchesControl();
    }

    // ####################### PREFERENCES ##################################

    PreferenceEditor preferenceEditor;

    @DataModel(value = "userPreferenceEntities")
    private List<PreferenceEntity> userPreferenceEntities;

    @Factory("userPreferenceEntities")
    public void initPreferencesEditor() {
        preferenceEditor = (PreferenceEditor)Component.getInstance("prefEditor");
        preferenceEditor.setVisibilities(new PreferenceVisibility[] {PreferenceVisibility.USER});
        preferenceEditor.setUser(getInstance());
        userPreferenceEntities = preferenceEditor.getPreferenceEntities();
        Contexts.getConversationContext().set("preferenceEditor", preferenceEditor);
    }

}

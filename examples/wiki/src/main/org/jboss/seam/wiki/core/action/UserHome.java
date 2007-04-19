package org.jboss.seam.wiki.core.action;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.core.Renderer;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.security.AuthorizationException;
import org.jboss.seam.security.Identity;
import org.jboss.seam.wiki.core.action.prefs.UserManagementPreferences;
import org.jboss.seam.wiki.core.action.prefs.WikiPreferences;
import org.jboss.seam.wiki.core.dao.UserDAO;
import org.jboss.seam.wiki.core.model.Role;
import org.jboss.seam.wiki.core.model.User;
import org.jboss.seam.wiki.preferences.PreferenceComponent;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;
import org.jboss.seam.wiki.util.Hash;

import javax.faces.application.FacesMessage;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Name("userHome")
@Scope(ScopeType.CONVERSATION)
public class UserHome extends EntityHome<User> {

    @RequestParameter
    private Long userId;

    @In
    private FacesMessages facesMessages;

    @In
    private UserDAO userDAO;

    @In
    private UserManagementPreferences userManagementPreferences;

    @In
    NodeBrowser browser;

    @In
    private Hash hashUtil;

    @In
    private Renderer renderer;

    @DataModel
    private List<PreferenceComponent> userPreferenceComponents;
    PreferenceEditor preferenceEditor;

    private String oldUsername;
    private String password;
    private String passwordControl;
    private List<Role> roles;
    private org.jboss.seam.wiki.core.model.Role defaultRole;

    @Override
    public Object getId() {

        if (userId == null) {
            return super.getId();
        } else {
            return userId;
        }
    }

    @Transactional
    public void create() {
        super.create();

        if (isManaged()) {
            if (!Identity.instance().hasPermission("User", "edit", getInstance()) ) {
                throw new AuthorizationException("You don't have permission for this operation");
            }
            roles = getInstance().getRoles();
            oldUsername = getInstance().getUsername();

        } else {
            UserManagementPreferences userMgmtPrefs =
                    (UserManagementPreferences) Component.getInstance("userManagementPreferences");
            if (!userMgmtPrefs.isEnableRegistration())
                throw new RuntimeException("User registration has been disabled");
            defaultRole = (Role)Component.getInstance("newUserDefaultRole");
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

        // Set activation code (unique user in time)
        String seed = getInstance().getUsername() + System.currentTimeMillis() + userManagementPreferences.getActivationCodeSalt();
        getInstance().setActivationCode( ((Hash)Component.getInstance("hashUtil")).hash(seed) );

        String outcome = super.persist();
        if (outcome != null) {

            try {

                // Send confirmation email
                renderer.render("/themes/"
                        + ((WikiPreferences)Component.getInstance("wikiPreferences")).getThemeName()
                        + "/mailtemplates/confirmationRegistration.xhtml");

                // Redirect to last viewed page with message
                facesMessages.addFromResourceBundleOrDefault(
                    FacesMessage.SEVERITY_INFO,
                    getMessageKeyPrefix() + "confirmationEmailSent",
                    "A confirmation e-mail has been sent to '" + getInstance().getEmail() + "'. " +
                    "Please read this e-mail to activate your account.");

                /* For debugging
                facesMessages.addFromResourceBundleOrDefault(
                    FacesMessage.SEVERITY_INFO,
                    getMessageKeyPrefix() + "confirmationEmailSent",
                    "Activiate account: /confirmRegistration.seam?activationCode=" + getInstance().getActivationCode());
                */

                browser.exitConversation(false);

            } catch (Exception ex) {
                facesMessages.add(FacesMessage.SEVERITY_ERROR, "Couldn't send confirmation email: " + ex.getMessage());
                return "error";
            }
        }

        return outcome;
    }

    @Restrict("#{s:hasPermission('User', 'edit', userHome.instance)}")
    public String update() {

        // Validate
        if (!isUniqueUsername())
                return null;

        // Roles
        getInstance().getRoles().clear();
        getInstance().getRoles().addAll(roles);

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
        if (!getInstance().getUsername().equals(oldUsername)) loginCredentialsModified = true;

        String outcome = super.update();
        if (outcome != null) {

            User currentUser = (User)Component.getInstance("currentUser");
            if (getInstance().getId().equals(currentUser.getId())) {
                // Updated profile of currently logged-in user
                Contexts.getSessionContext().set("currentUser", getInstance());

                // TODO: If identity.logout() wouldn't kill my session, I could call it here...
                // And I don't have cleartext password in all cases, so I can't relogin the user automatically
                if (loginCredentialsModified) {
                    facesMessages.addFromResourceBundleOrDefault(
                        FacesMessage.SEVERITY_INFO,
                        getMessageKeyPrefix() + "reloginRequired",
                        "Credentials updated, please logout and authenticate yourself with the new credentials."
                    );
                    browser.exitConversation(false);
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

    protected String getCreatedMessageKey() {
        return getMessageKeyPrefix() + "registrationComplete";
    }

    public String getCreatedMessage() {
        return "Your account '" + getInstance().getUsername() + "' has been created.";
    }

    protected String getUpdatedMessageKey() {
        return getMessageKeyPrefix() + "profileUpdated";
    }

    public String getUpdatedMessage() {
        return "The user '" + getInstance().getUsername() + "' has been updated.";
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
            getMessageKeyPrefix() + "homeDirectoryCreated",
            "New home directory has been queued, update to commit the change"
        );
    }

    @Factory("userPreferenceComponents")
    public void loadUserPreferenceComponents() {
        preferenceEditor = (PreferenceEditor)Component.getInstance("prefEditor");
        preferenceEditor.setPreferenceVisibility(PreferenceVisibility.USER);
        preferenceEditor.setUser(getInstance());
        userPreferenceComponents = preferenceEditor.loadPreferenceComponents();
        Contexts.getConversationContext().set("preferenceEditor", preferenceEditor);
    }

    // Validation rules for persist(), update(), and remove();

    public boolean passwordAndControlNotNull() {
        if (getPassword() == null || getPassword().length() == 0 ||
            getPasswordControl() == null || getPasswordControl().length() == 0) {
            facesMessages.addToControlFromResourceBundleOrDefault(
                "passwordControl",
                FacesMessage.SEVERITY_ERROR,
                getMessageKeyPrefix() + "passwordOrPasswordControlEmpty",
                "Please enter your password twice."
            );
            return false;
        }
        return true;
    }

    public boolean passwordMatchesRegex() {
        Matcher matcher = Pattern.compile(userManagementPreferences.getPasswordRegex()).matcher(getPassword());
        if (!matcher.find()) {
            facesMessages.addToControlFromResourceBundleOrDefault(
                "password",
                FacesMessage.SEVERITY_ERROR,
                getMessageKeyPrefix() + "passwordNoRegexMatch",
                "Password does not match the pattern: " + userManagementPreferences.getPasswordRegex()
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
                getMessageKeyPrefix() + "passwordControlNoMatch",
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
                getMessageKeyPrefix() + "usernameExists",
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

}

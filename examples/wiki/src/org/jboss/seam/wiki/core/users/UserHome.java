package org.jboss.seam.wiki.core.users;


import org.jboss.seam.annotations.*;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.core.Renderer;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.wiki.core.dao.UserDAO;
import org.jboss.seam.wiki.core.prefs.GlobalPreferences;
import org.jboss.seam.wiki.core.node.NodeBrowser;
import org.jboss.seam.wiki.util.Hash;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;

import javax.faces.application.FacesMessage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Name("userHome")
public class UserHome extends EntityHome<User> {

    @RequestParameter
    private Long userId;

    @In
    private FacesMessages facesMessages;

    @In(create = true)
    private UserDAO userDAO;

    @In(create = true)
    private Hash hashUtil;

    @In
    private GlobalPreferences globalPrefs;

    @In(required = false)
    @Out(required = false, scope = ScopeType.SESSION)
    private User authenticatedUser;

    @In
    private Renderer renderer;

    private Role defaultRole;

    private String oldUsername;
    private String password;
    private String passwordControl;

    @Override
    public Object getId() {

        if (userId == null) {
            return super.getId();
        } else {
            return userId;
        }
    }

    @Begin(flushMode = FlushModeType.MANUAL, join = true)
    @Transactional
    public void create() {
        super.create();

        defaultRole = userDAO.findRole(globalPrefs.getNewUserInRole());
        if (defaultRole == null) throw new RuntimeException("Default role for new users not configured");

        oldUsername = getInstance().getUsername();
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
        getInstance().addRole(defaultRole);

        // Set password hash
        getInstance().setPasswordHash(hashUtil.hash(getPassword()));

        // Set activation code (unique user in time)
        String seed = getInstance().getUsername() + System.currentTimeMillis() + globalPrefs.getActivationCodeSalt();
        getInstance().setActivationCode( ((Hash)Component.getInstance("hashUtil")).hash(seed) );

        String outcome = super.persist();
        if (outcome != null) {

            try {
                // Send confirmation email
                renderer.render("/themes/" + globalPrefs.getThemeName() + "/mailtemplates/confirmationRegistration.xhtml");

                // Redirect to last viewed page with message
                facesMessages.addFromResourceBundleOrDefault(
                    FacesMessage.SEVERITY_INFO,
                    getMessageKeyPrefix() + "confirmationEmailSent",
                    "A confirmation e-mail has been sent to '" + getInstance().getEmail() + "'. " +
                    "Please read this e-mail to activate your account.");

                ((NodeBrowser) Component.getInstance(NodeBrowser.class)).redirectToLastBrowsedPageWithConversation();

            } catch (Exception ex) {
                facesMessages.add(FacesMessage.SEVERITY_ERROR, "Couldn't send confirmation email: " + ex.getMessage());
                return "error";
            }
        }

        return outcome;
    }


    public String update() {

        // Validate
        if (!isUniqueUsername())
                return null;

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

            if (getInstance().getId().equals(authenticatedUser.getId())) {
                // Updated profile of currently logged-in user
                authenticatedUser = getInstance();

                // TODO: If identity.logout() wouldn't kill my session, I could call it here...
                // And I don't have cleartext password in all cases, so I can't relogin the user automatically
                if (loginCredentialsModified) {
                    facesMessages.addFromResourceBundleOrDefault(
                        FacesMessage.SEVERITY_INFO,
                        getMessageKeyPrefix() + "reloginRequired",
                        "Credentials updated, please logout and authenticate yourself with the new credentials."
                    );
                }
            }
            ((NodeBrowser) Component.getInstance(NodeBrowser.class)).redirectToLastBrowsedPageWithConversation();
        }

        return outcome;
    }

    public String remove() {

        // Remove all role assignments
        getInstance().getRoles().clear();

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
        return "The profile '" + getInstance().getUsername() + "' has been updated.";
    }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPasswordControl() { return passwordControl; }
    public void setPasswordControl(String passwordControl) { this.passwordControl = passwordControl; }


    // Validation rules for persist(), update(), and remove();

    private boolean passwordAndControlNotNull() {
        if (getPassword() == null || getPassword().length() == 0 ||
            getPasswordControl() == null || getPasswordControl().length() == 0) {
            facesMessages.addToControlFromResourceBundleOrDefault(
                "password",
                FacesMessage.SEVERITY_ERROR,
                getMessageKeyPrefix() + "passwordOrPasswordControlEmpty",
                "Please enter your password twice."
            );
            return false;
        }
        return true;
    }


    private boolean passwordMatchesRegex() {
        Matcher matcher = Pattern.compile(globalPrefs.getPasswordRegex()).matcher(getPassword());
        if (!matcher.find()) {
            facesMessages.addToControlFromResourceBundleOrDefault(
                "password",
                FacesMessage.SEVERITY_ERROR,
                getMessageKeyPrefix() + "passwordNoRegexMatch",
                "Password does not match the pattern: " + globalPrefs.getPasswordRegex()
            );
            return false;
        }
        return true;
    }

    private boolean passwordMatchesControl() {
        if (!password.equals(passwordControl) ) {
            facesMessages.addToControlFromResourceBundleOrDefault(
                "password",
                FacesMessage.SEVERITY_ERROR,
                getMessageKeyPrefix() + "passwordControlNoMatch",
                "The passwords don't match."
            );
            return false;
        }
        return true;
    }

    @Transactional
    private boolean isUniqueUsername() {
        getEntityManager().joinTransaction();
        User foundUser = userDAO.findUser(getInstance().getUsername(), false);
        if ( foundUser != null && foundUser != getInstance()) {
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

}

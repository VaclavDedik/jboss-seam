package org.jboss.seam.wiki.core.action.prefs;

import org.jboss.seam.wiki.preferences.PreferenceSupport;
import org.jboss.seam.wiki.preferences.Preference;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.ScopeType;
import org.hibernate.validator.Length;

import java.io.Serializable;

@Name("userManagementPreferences")
@AutoCreate
@Scope(ScopeType.PAGE)
@Preference(description = "A. User Management", visibility = PreferenceVisibility.SYSTEM)
public class UserManagementPreferences extends PreferenceSupport implements Serializable {

    @Preference(description = "01. Secret salt used to generate activation codes", visibility = PreferenceVisibility.SYSTEM)
    @Length(min = 5, max = 20)
    private String activationCodeSalt;

    @Preference(description = "02. Regex used for password strength verification", visibility = PreferenceVisibility.SYSTEM)
    @Length(min = 2, max = 100)
    private String passwordRegex;

    @Preference(description = "03. Default role name of new users after registration", visibility = PreferenceVisibility.SYSTEM)
    @Length(min = 3, max = 255)
    private String newUserInRole;

    @Preference(description = "04. Enable free user registration", visibility = PreferenceVisibility.SYSTEM)
    private boolean enableRegistration;

    @Preference(description = "05. Create home directory for new user after activation", visibility = PreferenceVisibility.SYSTEM)
    private boolean createHomeAfterUserActivation;

    public String getActivationCodeSalt() {
        return activationCodeSalt;
    }

    public String getPasswordRegex() {
        return passwordRegex;
    }

    public String getNewUserInRole() {
        return newUserInRole;
    }

    public boolean isEnableRegistration() {
        return enableRegistration;
    }

    public boolean isCreateHomeAfterUserActivation() {
        return createHomeAfterUserActivation;
    }
}

package org.jboss.seam.wiki.core.action;

import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Component;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.AuthorizationException;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.wiki.preferences.*;
import org.jboss.seam.wiki.preferences.PreferenceRegistry;
import org.jboss.seam.wiki.core.dao.UserDAO;
import org.jboss.seam.wiki.core.model.User;
import org.hibernate.validator.InvalidValue;

import javax.faces.application.FacesMessage;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashSet;
import java.io.Serializable;

@Name("prefEditor")
@Scope(ScopeType.CONVERSATION)
public class PreferenceEditor implements Serializable {

    @RequestParameter String visibility;
    @RequestParameter Long userId;

    @In
    private FacesMessages facesMessages;

    @DataModel
    private List<PreferenceComponent> preferenceComponents;

    @DataModelSelection
    private PreferenceComponent selectedPreferenceComponent;

    private PreferenceVisibility preferenceVisibility;
    private Object user;

    private PreferenceComponent preferenceComponent;
    private List<PreferenceValue> preferenceValues;

    @Create
    public void create() {
        if (PreferenceVisibility.USER.name().equals(visibility)) {
            preferenceVisibility = PreferenceVisibility.USER;
            loadUser();
        } else {
            if (!Identity.instance().hasPermission("User", "isAdmin", (User)Component.getInstance("currentUser")) ) {
                throw new AuthorizationException("You don't have permission for this operation");
            }
            preferenceVisibility = PreferenceVisibility.SYSTEM;
        }
    }

    @Factory("preferenceComponents")
    public void load() {
        PreferenceRegistry registry = (PreferenceRegistry)Component.getInstance("preferenceRegistry");
        preferenceComponents =
                new ArrayList<PreferenceComponent>(registry.getPreferenceComponents(preferenceVisibility));
    }

    @Transactional
    public String save() {

        boolean validationOk = true;
        Map<PreferenceProperty, InvalidValue[]> invalidProperties = preferenceComponent.validate(preferenceValues);
        for (Map.Entry<PreferenceProperty, InvalidValue[]> entry : invalidProperties.entrySet()) {
            for (InvalidValue validationError : entry.getValue()) {
                validationOk = false;

                facesMessages.addFromResourceBundleOrDefault(
                    FacesMessage.SEVERITY_ERROR,
                    "preferenceValueValidationFailed." + preferenceComponent.getName() + "." + entry.getKey().getName(),
                    "'" + entry.getKey().getDescription() + "': " + validationError.getMessage());
            }
        }

        if (!validationOk) return null;

        PreferenceProvider provider = (PreferenceProvider)Component.getInstance("preferenceProvider");
        if (preferenceVisibility.equals(PreferenceVisibility.USER)) {
            // Store prefs for a user
            preferenceValues = new ArrayList<PreferenceValue>(provider.store(preferenceComponent, new HashSet<PreferenceValue>(preferenceValues), user, null));
        } else {
            // Store system prefs
            preferenceValues = new ArrayList<PreferenceValue>(provider.store(preferenceComponent, new HashSet<PreferenceValue>(preferenceValues), null, null));
        }
        provider.flush();

        facesMessages.addFromResourceBundleOrDefault(
            FacesMessage.SEVERITY_INFO,
            "wikiPreferencesSaved",
            "Preferences have been saved, continue editing or exit.");
        return null;
    }

    public void selectPreferenceComponent() {
        preferenceComponent = selectedPreferenceComponent;

        PreferenceProvider provider = (PreferenceProvider)Component.getInstance("preferenceProvider");

        if (preferenceVisibility.equals(PreferenceVisibility.USER)) {
            // Load prefs for a user
            preferenceValues = new ArrayList<PreferenceValue>(provider.load(preferenceComponent, user, null, false));
        } else {
            // Load system prefs
            preferenceValues = new ArrayList<PreferenceValue>(provider.load(preferenceComponent, null, null, true));
        }
    }

    public PreferenceComponent getPreferenceComponent() {
        return preferenceComponent;
    }

    public List<PreferenceValue> getPreferenceValues() {
        return preferenceValues;
    }

    protected void loadUser() {
        if (userId != null) {
            UserDAO userDAO = (UserDAO)Component.getInstance("userDAO");
            user = userDAO.findUser(userId);
        } else {
            user = Component.getInstance("currentUser");
        }
    }

}

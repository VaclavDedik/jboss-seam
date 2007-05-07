package org.jboss.seam.wiki.core.action;

import org.jboss.seam.annotations.*;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Component;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.wiki.preferences.*;
import org.jboss.seam.wiki.preferences.PreferenceRegistry;
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

    @In
    private FacesMessages facesMessages;

    private User user;
    private PreferenceVisibility preferenceVisibility;

    private PreferenceComponent preferenceComponent;
    private List<PreferenceValue> preferenceValues;
    boolean valid = true;

    public String save() {
        if (preferenceComponent == null) return null;

        validate();
        if (!valid) return "failed";

        PreferenceProvider provider = (PreferenceProvider)Component.getInstance("preferenceProvider");
        if (preferenceVisibility.equals(PreferenceVisibility.USER)) {
            // Store prefs for a user
            preferenceValues = new ArrayList<PreferenceValue>(
                provider.store(preferenceComponent, new HashSet<PreferenceValue>(preferenceValues), user, null)
            );
        } else {
            // Store system prefs
            preferenceValues = new ArrayList<PreferenceValue>(
                provider.store(preferenceComponent, new HashSet<PreferenceValue>(preferenceValues), null, null)
            );
        }
        provider.flush();

        facesMessages.addToControlFromResourceBundleOrDefault(
            "preferenceValidationErrors",
            FacesMessage.SEVERITY_INFO,
            "preferencesSaved." + preferenceComponent.getName(),
            "Preferences have been saved, continue editing or exit.");
        return null;
    }

    public void validate() {
        if (preferenceComponent == null) return;
        valid = true;
        Map<PreferenceProperty, InvalidValue[]> invalidProperties = preferenceComponent.validate(preferenceValues);
        for (Map.Entry<PreferenceProperty, InvalidValue[]> entry : invalidProperties.entrySet()) {
            for (InvalidValue validationError : entry.getValue()) {
                valid = false;

                facesMessages.addToControlFromResourceBundleOrDefault(
                    "preferenceValidationErrors",
                    FacesMessage.SEVERITY_ERROR,
                    "preferenceValueValidationFailed." + preferenceComponent.getName() + "." + entry.getKey().getName(),
                    preferenceComponent.getDescription() + " - '" + entry.getKey().getDescription() + "': " + validationError.getMessage());
            }
        }
    }

    public void selectPreferenceComponent(PreferenceComponent selectedPreferenceComponent) {
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

    public List<PreferenceComponent> loadPreferenceComponents() {
        PreferenceRegistry registry = (PreferenceRegistry)Component.getInstance("preferenceRegistry");
        return new ArrayList<PreferenceComponent>(registry.getPreferenceComponents(preferenceVisibility));
    }

    public PreferenceComponent getPreferenceComponent() {
        return preferenceComponent;
    }

    public List<PreferenceValue> getPreferenceValues() {
        return preferenceValues;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setPreferenceVisibility(PreferenceVisibility preferenceVisibility) {
        this.preferenceVisibility = preferenceVisibility;
    }

    public boolean isValid() {
        return valid;
    }
}

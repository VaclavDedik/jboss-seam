package org.jboss.seam.wiki.core.action;

import org.jboss.seam.wiki.preferences.*;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.core.Events;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.AutoCreate;
import org.hibernate.validator.InvalidValue;

import javax.faces.application.FacesMessage;
import java.util.*;
import java.io.Serializable;

public class PluginPreferenceEditor implements Serializable {

    private String pluginPreferenceName;
    private PreferenceComponent preferenceComponent;
    private List<PreferenceValue> preferenceValues = new ArrayList<PreferenceValue>();

    public PluginPreferenceEditor(String pluginPreferenceName) {
        System.out.println("#### NEW EDITOR FOR PLUGIN: " + pluginPreferenceName);

        this.pluginPreferenceName = pluginPreferenceName;

        // Load the preference component
        PreferenceRegistry registry = (PreferenceRegistry) Component.getInstance("preferenceRegistry");
        preferenceComponent = registry.getPreferenceComponentsByName().get(pluginPreferenceName);

        if (preferenceComponent != null) {
            // Materialize its values

            Object user = Component.getInstance("currentUser");
            Object instance = Component.getInstance("currentDocument");
            PreferenceProvider provider = (PreferenceProvider)Component.getInstance("preferenceProvider");

            preferenceValues = new ArrayList<PreferenceValue>(provider.load(preferenceComponent, user, instance, false));
        }

    }

    public List<PreferenceValue> getPreferenceValues() {
        System.out.println("######### GET PREFERENCE VALUES FROM PLUGIN EDITOR");
        return preferenceValues;
    }

    public void apply() {

        if (preferenceValues.size() > 0 ) {

            boolean validationOk = true;
            Map<PreferenceProperty, InvalidValue[]> invalidProperties = preferenceComponent.validate(preferenceValues);
            for (Map.Entry<PreferenceProperty, InvalidValue[]> entry : invalidProperties.entrySet()) {
                for (InvalidValue validationError : entry.getValue()) {
                    validationOk = false;
                    FacesMessages.instance().addToControlFromResourceBundleOrDefault(
                        pluginPreferenceName,
                        FacesMessage.SEVERITY_ERROR,
                        "preferenceValueValidationFailed." + preferenceComponent.getName() + "." + entry.getKey().getName(),
                        "'" + entry.getKey().getDescription() + "': " + validationError.getMessage());
                }
            }

            if (validationOk) {
                Object user = Component.getInstance("currentUser");
                Object instance = Component.getInstance("currentDocument");
                PreferenceProvider provider = (PreferenceProvider)Component.getInstance("preferenceProvider");
                preferenceValues = new ArrayList<PreferenceValue>(provider.store(preferenceComponent, new HashSet<PreferenceValue>(preferenceValues), user, instance));

                Events.instance().raiseEvent("PreferenceEditor.refresh." + preferenceComponent.getName());
            }
        }

    }

    public void flush() {
        PreferenceProvider provider = (PreferenceProvider)Component.getInstance("preferenceProvider");
        provider.flush();
    }

    @Name("pluginPreferenceEditorFlushObserver")
    @Scope(ScopeType.CONVERSATION)
    @AutoCreate
    public static class FlushObserver implements Serializable {

        Set<PluginPreferenceEditor> editors = new HashSet<PluginPreferenceEditor>();

        public void addPluginPreferenceEditor(PluginPreferenceEditor editor) {
            editors.add(editor);
        }

        @Observer("PreferenceEditor.flushAll")
        public void flushEditors() {
            for (PluginPreferenceEditor editor : editors) {
                editor.flush();
            }
        }
    }

    
}

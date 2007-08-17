/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.preferences;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.core.Events;
import org.jboss.seam.log.Log;
import org.jboss.seam.wiki.plugin.lastmodified.LastModifiedDocumentsPreferences;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Superclass for definition of the preferences meta data.
 * <p>
 * Subclass this class with a Seam component in <tt>CONVERSATION</tt> scope and apply
 * <tt>@Preference</tt> annotations to define your preferences.
 * <p>
 * Think about grouping of preference values: Each Seam component that subclasses this
 * class is a group of preferences, the name of the group is the name of the Seam component.
 * Each property of the subclass, or even each field (whatever is annotated with <tt>@Preference</tt>)
 * is a preference property that can be loaded and stored with an implementation of <tt>PreferenceProvider</tt>.
 * </p>
 * <p>
 * You can access preference properties transparently with either
 * <tt>#{seamNameOfThePreferenceComponent.properties['prefPropertyName']}</tt> or with
 * <tt>#{seamNameOfThePreferenceComponent.prefProperty}</tt> if there are property accessor methods, or even
 * type-safe by getting the  whole <tt>#{seamNameOfThePreferenceComponent}</tt> injected.
 * </p>
 * <p>
 * Note that you should add <tt>@NotNull</tt> to any preference property that is critical in addition to any
 * other pattern, length, range, or other validator annotations.
 * </p>
 * <p>
 * Subclasses should be in <tt>CONVERSATION</tt> or <tt>PAGE</tt> scope.
 * </p>
 * <p>
 * Subclasses automatically read preference properties when they are instantiated for the current conversation or page.
 * Subclasses are  automatically notified to refresh their property values inside a conversation, however, you need
 * to call the method <tt>super.refreshProperties()</tt> in your subclass in a method that has the event listener
 * <tt>@Observer("PreferenceEditor.refresh.seamNameOfThePreferenceComponent")</tt> to enable this functionality.
 * This is only used if preference values can change during a conversation, typically when a preference editor
 * is available to the user in that conversation.
 * </p>
 * <p>
 * You can notify all users of a preference component when a preference property value is changed during the
 * same conversation in which the preference values are used. The event
 * <tt>@Observer("PreferenceComponent.refresh.seamNameOfThePreferenceComponent")</tt> is fired when preference values are
 * re-loaded/loaded and you can put it on any method that needs to re-read some state after a preference value
 * change. Note again that this is mostly useful inside a conversation, instances of this class should not
 * live longer than a conversation.
 * </p>
 * <p>
 * Override the <tt>getCurrentUserVariable</tt> and <tt>getCurrentInstanceVariable</tt> with EL expressions or
 * context variable names if you want to use a particular user or instance for lookup of the preference values.
 * These methods default to null and only system-level preference values are resolved.
 * </p>
 *
 * @author Christian Bauer
 */
public abstract class PreferenceSupport {

    @Logger Log log;

    private Map<String, Object> properties;

    @Create
    public void materialize() {
        this.properties = loadPropertyValues();
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    private Map<String, Object> loadPropertyValues() {
        PreferenceRegistry registry = (PreferenceRegistry) Component.getInstance("preferenceRegistry");
        PreferenceProvider provider = (PreferenceProvider) Component.getInstance("preferenceProvider");
        Object user = getCurrentUserVariable() != null ? Component.getInstance(getCurrentUserVariable()) : null;
        Object instance = getCurrentInstanceVariable() != null ? Component.getInstance(getCurrentInstanceVariable()) : null;

        PreferenceComponent prefComponent =
            registry.getPreferenceComponentsByName().get( Component.getComponentName(getClass()) );

        try {
            Map<String, Object> loadedProperties = new HashMap<String, Object>();

            Set<PreferenceValue> valueHolders = provider.load(prefComponent, user, instance, true);
            for (PreferenceValue valueHolder : valueHolders) {
                log.trace("loaded preference property value: '" + valueHolder.getPreferenceProperty().getName()
                          + "' value is: '" + valueHolder.getValue() + "'");

                // Write onto instance so users can call #{myPrefs.thisPreferenceSetting}
                valueHolder.getPreferenceProperty().write(this, valueHolder.getValue());

                // Keep a duplicate in this map so users can call #{myPrefs.properties['thisPreferenceSetting']}
                loadedProperties.put(valueHolder.getPreferenceProperty().getName(), valueHolder.getValue());

            }
            return loadedProperties;
        } catch (Exception ex) {
            log.warn("Could not write preference property value on component: " + prefComponent.getName(), ex);
            throw new RuntimeException(ex);
        }

    }

    public void refreshProperties() {
        log.debug("refreshing preference component property values");
        this.properties = loadPropertyValues();
    }

    public String getCurrentUserVariable() { return null; }
    public String getCurrentInstanceVariable() {return null; }
}

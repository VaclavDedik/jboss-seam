package org.jboss.seam.wiki.preferences;

import org.jboss.seam.annotations.*;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Component;
import org.jboss.seam.log.Log;
import org.jboss.seam.contexts.Contexts;

import java.util.*;

/**
 * Reads all components and properties on such components with a <tt>@Preference</tt> annotation.
 * <tt>
 * You can access the preference meta model, e.g. to build a dynamic preference editor, by looking up
 * this registry with <tt>#{preferenceRegistry}</tt> and accessing the metadata through the
 * <tt>preferenceComponents</tt> and <tt>preferenceComponentsByName</tt> collections.
 *
 * @author Christian Bauer
 */
@Name("preferenceRegistry")
@Scope(ScopeType.APPLICATION)
@Startup(depends = "wikiInit")
public class PreferenceRegistry {

    @Logger static Log log;

    SortedSet<PreferenceComponent> preferenceComponents = new TreeSet<PreferenceComponent>();
    Map<String, PreferenceComponent> preferenceComponentsByName = new HashMap<String, PreferenceComponent>();

    @Create
    public void scanPreferenceComponents() {
        // Register the meta model by scanning components with @Preference annotation
        String[] boundNames = Contexts.getApplicationContext().getNames();

        for (String boundName : boundNames) {
            if (boundName.endsWith(".component") && !boundName.startsWith("org.jboss.seam")) {
                Component component = (Component) Contexts.getApplicationContext().get(boundName);

                if (component.getBeanClass().getAnnotation(Preference.class) != null &&
                    !preferenceComponentsByName.containsKey(component.getName())) {

                    log.debug("Registering preference component: " + component.getName());
                    PreferenceComponent prefComponent = new PreferenceComponent(component);
                    preferenceComponentsByName.put(prefComponent.getName(), prefComponent);
                    preferenceComponents.add(prefComponent);
                }
            }
        }
    }

    public Map<String, PreferenceComponent> getPreferenceComponentsByName() {
        return preferenceComponentsByName;
    }

    public SortedSet<PreferenceComponent> getPreferenceComponents() {
        return preferenceComponents;
    }

    public SortedSet<PreferenceComponent> getPreferenceComponents(PreferenceVisibility visibility) {
        SortedSet<PreferenceComponent> filteredComponents = new TreeSet<PreferenceComponent>();
        for (PreferenceComponent preferenceComponent : preferenceComponents) {
            if (preferenceComponent.getVisibility().ordinal() >= visibility.ordinal()) filteredComponents.add(preferenceComponent);
        }
        return filteredComponents;
    }
}

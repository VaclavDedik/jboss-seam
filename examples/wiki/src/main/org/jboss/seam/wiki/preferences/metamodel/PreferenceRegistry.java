package org.jboss.seam.wiki.preferences.metamodel;

import org.jboss.seam.ScopeType;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;
import org.jboss.seam.log.Log;

import java.util.*;

@Name("preferenceRegistry")
@Scope(ScopeType.APPLICATION)
public class PreferenceRegistry {

    @Logger
    static Log log;

    Set<PreferenceEntity> preferenceEntities = new HashSet<PreferenceEntity>();
    Map<String, PreferenceEntity> preferenceEntitiesByName = new HashMap<String, PreferenceEntity>();

    Set<PreferenceEntity> preferenceEntitiesSystem = new HashSet<PreferenceEntity>();
    Set<PreferenceEntity> preferenceEntitiesUser = new HashSet<PreferenceEntity>();
    Set<PreferenceEntity> preferenceEntitiesInstance = new HashSet<PreferenceEntity>();

    @Observer("Wiki.started")
    public void scanForPreferencesSupportComponents() {

        log.debug("initializing preferences registry");

        // Fire an event and let all listeners add themself into the given collection
        Set<PreferencesSupport> preferencesSupportComponents = new HashSet<PreferencesSupport>();
        Events.instance().raiseEvent("Preferences.addPreferencesSupport", preferencesSupportComponents);

        log.debug("found preferences support components: " + preferencesSupportComponents.size());

        for (PreferencesSupport component : preferencesSupportComponents) {

            for (PreferenceEntity preferenceEntity : component.getPreferenceEntities()) {
                log.debug("adding '" + preferenceEntity.getEntityName() + "', " + preferenceEntity);

                if (preferenceEntitiesByName.containsKey(preferenceEntity.getEntityName())) {
                    throw new RuntimeException("Duplicate preference entity name: " + preferenceEntity.getEntityName());
                }

                preferenceEntities.add(preferenceEntity);
                preferenceEntitiesByName.put(preferenceEntity.getEntityName(), preferenceEntity);

                if (preferenceEntity.isSystemPropertiesVisible())
                    preferenceEntitiesSystem.add(preferenceEntity);
                if (preferenceEntity.isUserPropertiesVisible())
                    preferenceEntitiesUser.add(preferenceEntity);
                if (preferenceEntity.isInstancePropertiesVisible())
                    preferenceEntitiesInstance.add(preferenceEntity);
            }
        }
    }

    public Set<PreferenceEntity> getPreferenceEntities() {
        return preferenceEntities;
    }

    public Map<String, PreferenceEntity> getPreferenceEntitiesByName() {
        return preferenceEntitiesByName;
    }

    public Set<PreferenceEntity> getPreferenceEntitiesSystem() {
        return preferenceEntitiesSystem;
    }

    public Set<PreferenceEntity> getPreferenceEntitiesUser() {
        return preferenceEntitiesUser;
    }

    public Set<PreferenceEntity> getPreferenceEntitiesInstance() {
        return preferenceEntitiesInstance;
    }

    public SortedSet<PreferenceEntity> getPreferenceEntities(PreferenceVisibility[] visibilities) {
        SortedSet<PreferenceEntity> entities = new TreeSet<PreferenceEntity>();
        List<PreferenceVisibility> visibilityList = Arrays.asList(visibilities);
        if (visibilityList.contains(PreferenceVisibility.SYSTEM)) entities.addAll(getPreferenceEntitiesSystem());
        if (visibilityList.contains(PreferenceVisibility.USER)) entities.addAll(getPreferenceEntitiesUser());
        if (visibilityList.contains(PreferenceVisibility.INSTANCE)) entities.addAll(getPreferenceEntitiesInstance());
        return entities;
    }
}
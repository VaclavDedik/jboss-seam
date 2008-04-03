package org.jboss.seam.wiki.preferences.metamodel;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.log.Log;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;

import java.util.*;

@Name("preferenceRegistry")
@Scope(ScopeType.APPLICATION)
public class PreferenceRegistry {

    @Logger
    static Log log;

    @In(
        value="#{deploymentStrategy.annotatedClasses['org.jboss.seam.wiki.preferences.annotations.Preferences']}",
        required = false
    )
    Set<Class> preferencesClasses;

    Set<PreferenceEntity> preferenceEntities = new HashSet<PreferenceEntity>();
    Map<String, PreferenceEntity> preferenceEntitiesByName = new HashMap<String, PreferenceEntity>();

    Set<PreferenceEntity> preferenceEntitiesSystem = new HashSet<PreferenceEntity>();
    Set<PreferenceEntity> preferenceEntitiesUser = new HashSet<PreferenceEntity>();
    Set<PreferenceEntity> preferenceEntitiesInstance = new HashSet<PreferenceEntity>();

    @Observer("Wiki.started")
    public void create() {
        log.debug("initializing preferences registry");

        if (preferencesClasses == null)
            throw new RuntimeException("Add @Preferences annotation to META-INF/seam-deployment.properties");

        for (Class preferencesClass : preferencesClasses) {
            PreferenceEntity preferenceEntity = new PreferenceEntity(preferencesClass);

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
package org.jboss.seam.wiki.preferences;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.log.Log;
import org.jboss.seam.wiki.preferences.metamodel.PreferenceRegistry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Name("preferences")
@Scope(ScopeType.CONVERSATION)
public class Preferences implements Serializable {

    @Logger
    Log log;

    @In
    PreferenceRegistry preferenceRegistry;

    @In
    org.jboss.seam.wiki.preferences.PreferenceProvider preferenceProvider;

    @In(value = "#{currentPreferencesUser}", required = false)
    Object currentUser;

    // Cache SYSTEM and USER-level preferences per conversation
    private Map<CacheKey, Object> conversationPreferencesCache = new HashMap<CacheKey, Object>();

    // Cache INSTANCE-level preferences per event
    @In(required = false) @Out(required = false, scope = ScopeType.EVENT)
    private Map<CacheKey, Object> eventPreferencesCache;

    public Object get(String preferenceEntityName) {
        return get(preferenceEntityName, null);
    }

    public Object get(String preferenceEntityName, Object instance) {
        log.trace("getting preferences for entity: " + preferenceEntityName +
                  " with current user: " + currentUser +
                  " and current instance: " + instance);
        
        if (!preferenceRegistry.getPreferenceEntitiesByName().containsKey(preferenceEntityName))
            throw new IllegalArgumentException("Preference entity not found in registry: " + preferenceEntityName);

        // Prepare caching
        CacheKey cacheKey = new CacheKey(preferenceEntityName);
        if (eventPreferencesCache == null) eventPreferencesCache = new HashMap<CacheKey, Object>();

        List<PreferenceVisibility> visibilities = new ArrayList<PreferenceVisibility>();
        visibilities.add(PreferenceVisibility.SYSTEM);

        if (currentUser != null) {
            visibilities.add(PreferenceVisibility.USER);
            cacheKey.setUser(currentUser);
        }
        if (instance != null ) {
            visibilities.add(PreferenceVisibility.INSTANCE);
            cacheKey.setInstance(instance);
        }

        // First check the caches
        if (conversationPreferencesCache.containsKey(cacheKey)) {
            log.trace("returning cached preference entity of current conversation");
            return conversationPreferencesCache.get(cacheKey);
        }

        if (eventPreferencesCache.containsKey(cacheKey)) {
            log.trace("returning cached preference entity of current event");
            return eventPreferencesCache.get(cacheKey);
        }

        // No cache hit, ask the provider for the values and assemble the instance
        Object preferenceEntityInstance =
            preferenceRegistry.getPreferenceEntitiesByName().get(preferenceEntityName).materialize(
                preferenceProvider.loadValues(preferenceEntityName, currentUser, instance, visibilities)
            );

        // Now put it in the right cache
        if (instance != null) {
            // INSTANCE-level goes into a per-event cache because the INSTANCE changes during a conversation
            eventPreferencesCache.put(cacheKey, preferenceEntityInstance);
        } else {
            // SYSTEM and USER-level goes into a per-conversation cache because they never change during a conversation
            conversationPreferencesCache.put(cacheKey, preferenceEntityInstance);
        }

        return preferenceEntityInstance;
    }

    public static Object getInstance(String preferenceEntityName, String instanceName) {
        return Expressions.instance().createValueExpression("#{preferences.get('" + preferenceEntityName + "', "+instanceName+")}").getValue();
    }

    public static Object getInstance(String preferenceEntityName) {
        return Expressions.instance().createValueExpression("#{preferences.get('" + preferenceEntityName + "')}").getValue();
    }

    private class CacheKey implements Serializable {
        private String entityName;
        private Object user;
        private Object instance;

        public CacheKey(String entityName) {
            this.entityName = entityName;
        }

        public void setUser(Object user) {
            this.user = user;
        }

        public void setInstance(Object instance) {
            this.instance = instance;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CacheKey cacheKey = (CacheKey) o;
            return !(entityName != null ? !entityName.equals(cacheKey.entityName) : cacheKey.entityName != null) &&
                    !(instance != null ? !instance.equals(cacheKey.instance) : cacheKey.instance!= null) &&
                    !(user != null ? !user.equals(cacheKey.user) : cacheKey.user != null);

        }

        public int hashCode() {
            int result;
            result = (entityName != null ? entityName.hashCode() : 0);
            result = 31 * result + (instance != null ? instance.hashCode() : 0);
            result = 31 * result + (user != null ? user.hashCode() : 0);
            return result;
        }
    }


}

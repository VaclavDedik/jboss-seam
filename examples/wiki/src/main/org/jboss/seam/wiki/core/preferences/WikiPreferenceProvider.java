package org.jboss.seam.wiki.core.preferences;

import org.jboss.seam.annotations.*;
import org.jboss.seam.wiki.core.model.User;
import org.jboss.seam.wiki.core.model.Node;
import org.jboss.seam.wiki.preferences.*;
import org.jboss.seam.ScopeType;
import org.jboss.seam.log.Log;
import org.hibernate.Session;
import org.hibernate.validator.InvalidValue;

import javax.persistence.EntityManager;
import java.util.*;
import java.io.Serializable;

/**
 * Implementation for the wiki, loads and stores <tt>WikiPreferenceValue</tt> objects.
 * <p>
 * This implementation tries to be as smart as possible and supports multi-level preference value overrides.
 * If you load values, they are automatically resolved for system, user, and instance levels. If you store
 * values, they are converted into the appropriate level (whatever the property setting of the meta model allows).
 *
 * @author Christian Bauer
 */
@Name("preferenceProvider")
@AutoCreate
@Scope(ScopeType.CONVERSATION)
public class WikiPreferenceProvider implements PreferenceProvider, Serializable {

    @Logger Log log;

    @In
    EntityManager entityManager;

    private Map<PreferenceComponent, Set<PreferenceValue>> currentValueMap = new HashMap<PreferenceComponent, Set<PreferenceValue>>();
    private Set<PreferenceValue> queuedNewValues = new HashSet<PreferenceValue>();

    @Transactional
    public Set<PreferenceValue> load(PreferenceComponent component, Object user, Object instance, boolean includeSystemPreferences) {
        log.debug("Loading preference values for component '" + component.getName() + "' and user '" + user + "' and instance '" + instance + "'");
        entityManager.joinTransaction();

        if (currentValueMap.get(component) != null) {
            log.debug("Returning cached preference values of current conversation");
            return currentValueMap.get(component);
        } else {
            log.debug("Resolving preference values and storing them in current conversation");
            currentValueMap.put(component, new TreeSet<PreferenceValue>());

            // Only load and instance preferences if there actually can be instance preferences
            if (instance != null && ((Node)instance).getId() != null && component.allowsInstanceOverride())
                loadInstanceValues(currentValueMap.get(component), component, (Node)instance);

            // Only load and user preferences if there actually can be user preferences
            if (user != null && component.allowsUserOverride())
                loadUserValues(currentValueMap.get(component), component, (User)user);

            // Always load system preferences
            loadSystemValues(currentValueMap.get(component), component, includeSystemPreferences);

            return currentValueMap.get(component);
        }
    }

    @Transactional
    public Set<PreferenceValue> store(PreferenceComponent component, Set<PreferenceValue> valueHolders, Object user, Object instance) {
        log.debug("Storing preference values for component '" + component.getName() + "' and user '" + user + "' and instance '" + instance + "'");
        entityManager.joinTransaction();

        currentValueMap.put(component, new TreeSet<PreferenceValue>());

        for (PreferenceValue valueHolder : valueHolders) {
            log.trace("Storing preference value: " + valueHolder.getPreferenceProperty().getName());

            if (instance != null &&
                    valueHolder.getPreferenceProperty().allowsInstanceOverride() &&
                    (valueHolder.isSystemAssigned() || valueHolder.isUserAssigned()) &&
                    valueHolder.isDirty()) {

                log.trace("New preference value object at INSTANCE level for property: " + valueHolder.getPreferenceProperty());

                WikiPreferenceValue newValueHolder = new WikiPreferenceValue(valueHolder.getPreferenceProperty());
                newValueHolder.setNode((Node) instance);
                newValueHolder.setValue(valueHolder.getValue());
                newValueHolder.setDirty(false); // New object is not "dirty"

                queuedNewValues.add(newValueHolder);
                getSession().evict(valueHolder);
                currentValueMap.get(component).add(newValueHolder);

            } else if (user != null &&
                    valueHolder.getPreferenceProperty().allowsUserOverride() &&
                    valueHolder.isSystemAssigned() &&
                    valueHolder.isDirty()) {

                log.trace("New preference value object at USER level for property: " + valueHolder.getPreferenceProperty());

                WikiPreferenceValue newValueHolder = new WikiPreferenceValue(valueHolder.getPreferenceProperty());
                newValueHolder.setUser((User) user);
                newValueHolder.setValue(valueHolder.getValue());
                newValueHolder.setDirty(false); // New object is not "dirty"

                queuedNewValues.add(newValueHolder);
                getSession().evict(valueHolder);
                currentValueMap.get(component).add(newValueHolder);
            } else {
                currentValueMap.get(component).add(valueHolder);
            }
        }

        return currentValueMap.get(component);
    }


    @Transactional
    public void deleteUserPreferences(Object user) {
        log.debug("Deleting all preference values of user '" + user + "'");
        entityManager.joinTransaction();
        entityManager.createQuery("delete from WikiPreferenceValue wp where wp.user = :user and wp.node is null")
                .setParameter("user", user)
                .executeUpdate();
    }

    @Transactional
    public void deleteInstancePreferences(Object instance) {
        log.debug("Deleting all preference values of instance '" + instance + "'");
        entityManager.joinTransaction();
        entityManager.createQuery("delete from WikiPreferenceValue wp where wp.user is null and wp.node = :node")
                .setParameter("node", instance)
                .executeUpdate();
    }

    @Transactional
    public void flush() {
        log.debug("Flushing queued preference values of this conversation to the database");
        entityManager.joinTransaction();

        // Persist new values (we need to do this during final flush because otherwise we have the persist()/identity generator problem
        for (PreferenceValue queuedNewValue : queuedNewValues) {
            log.trace("Persisting new preference value object: " + queuedNewValue);
            entityManager.persist(queuedNewValue);
        }

        // Don't flush invalid values
        // (invalid values at this point come from the plugin prefs editor, users might click Update even with wrong values in the form)
        for (Map.Entry<PreferenceComponent, Set<PreferenceValue>> entry : currentValueMap.entrySet()) {

            Set<PreferenceValue> invalidValues = new HashSet<PreferenceValue>();
            Map<PreferenceProperty, InvalidValue[]> validatedProperties = entry.getKey().validate(entry.getValue());
            for (Map.Entry<PreferenceProperty, InvalidValue[]> validationErrors: validatedProperties.entrySet()) {
                if (validationErrors.getValue().length >0) {
                    WikiPreferenceValue dummy = new WikiPreferenceValue(validationErrors.getKey());
                    invalidValues.add(dummy);
                }
            }

            for (PreferenceValue preferenceValue : entry.getValue()) {
                if (invalidValues.contains(preferenceValue)) {
                    log.trace("Evicting invalid preference value from persistence context before flush: " + preferenceValue);
                    getSession().evict(preferenceValue);
                }
            }
        }

        log.trace("Flushing persistence context");
        entityManager.flush();
    }

    /* #################### IMPLEMENTATION ######################### */

    private void loadSystemValues(Set<PreferenceValue> loadedValues, PreferenceComponent preferenceComponent, boolean includeSystemPreferences) {
        //noinspection unchecked
        List<WikiPreferenceValue> values =
            entityManager.createQuery(
                            "select wp from WikiPreferenceValue wp" +
                            " where wp.componentName = :name and wp.user is null and wp.node is null"
                          ).setParameter("name", preferenceComponent.getName()).getResultList();

        for (WikiPreferenceValue value : values) {
            PreferenceProperty property = preferenceComponent.getPropertiesByName().get( value.getPropertyName() );
            if (property == null)
                throw new RuntimeException("Orphaned preference value found in database, please clean up: " + value);
            value.setPreferenceProperty(property);
            if (value.getPreferenceProperty().allowsUserOverride() ||
                value.getPreferenceProperty().allowsInstanceOverride() ||
                includeSystemPreferences) {
                loadedValues.add(value);
            }
        }
    }

    private void loadUserValues(Set<PreferenceValue> loadedValues, PreferenceComponent preferenceComponent, User user) {
        //noinspection unchecked
        List<WikiPreferenceValue> values =
            entityManager.createQuery(
                            "select wp from WikiPreferenceValue wp" +
                            " where wp.componentName = :name and wp.user = :user and wp.node is null"
                          )
                        .setParameter("name", preferenceComponent.getName())
                        .setParameter("user", user)
                        .getResultList();
        for (WikiPreferenceValue value : values) {
            value.setPreferenceProperty(preferenceComponent.getPropertiesByName().get( value.getPropertyName() ));
            loadedValues.add(value);
        }
    }

    private void loadInstanceValues(Set<PreferenceValue> loadedValues, PreferenceComponent preferenceComponent, Node node) {
        //noinspection unchecked
        List<WikiPreferenceValue> values =
            entityManager.createQuery(
                            "select wp from WikiPreferenceValue wp" +
                            " where wp.componentName = :name and wp.user is null and wp.node = :node"
                          )
                        .setParameter("name", preferenceComponent.getName())
                        .setParameter("node", node)
                        .getResultList();
        for (WikiPreferenceValue value : values) {
            value.setPreferenceProperty(preferenceComponent.getPropertiesByName().get( value.getPropertyName() ));
            loadedValues.add(value);
        }
    }

    private Session getSession() {
        return (Session)entityManager.getDelegate();
    }

}

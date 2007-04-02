package org.jboss.seam.wiki.preferences;

import java.util.Set;

/**
 * Interface for loading and storing of preference values.
 * <p>
 * Implement this interface to load and store preference values grouped by "preference component". Expose
 * your implementation as an auto-created Seam component with the name <tt>preferenceProvider</tt>.
 * <p>
 *
 * @author Christian Bauer
 */
public interface PreferenceProvider {

    /**
     * Load preference values for a particular component.
     * <p>
     * The <tt>user</tt> and <tt>instance</tt> arguments can be null, in that case, you need to only load system
     * preference values (see <tt>PreferenceVisibility</tt>) for that component. If <tt>user</tt> and <tt>instance</tt>
     * are provided, you can return a combination of values that represent the override. E.g. if the component has
     * preference properties foo1, foo2, and foo3, and foo2 allows user override while foo3 allows instance override,
     * you can return three values which you looked up accordingly.
     * <p>
     * If <tt>includeSystemPreferences</tt> is true, the provider should return all system-level preference values even
     * if they do not allow user and instance override. If this argument is false, the provider should not return any
     * system-level preference values, unless these propertis allow override on the user and instance level. To understand
     * this, think about the two use cases how preference values are loaded: We need the "current" preference values, optionally
     * resolved against the given user and instance. We also need the "current" preference values for editing, in that case however,
     * we don't want to see any values that can't be edited.
     *
     * @param component the preference component meta data, read this to know what to load
     * @param user an optional (nullable) user argument useful for override lookup
     * @param instance an optional (nullable) instance argument useful for override lookup
     * @param includeSystemPreferences true if the provider should load
     * @return a set of <tt>PreferenceValue</tt> objects, can be a sorted set
     */
    public Set<PreferenceValue> load(PreferenceComponent component, Object user, Object instance, boolean includeSystemPreferences);

    /**
     * Store preference values for particular component.
     * <p>
     * This method should not directly and immediately store the preference values, but queue them in some way.
     * They should only be flushed to a permanent data store when <tt>flush</tt> is called.
     * 
     * @param component the preference component metadata for which the values should be stored
     * @param valueHolders the values to store, wrapped in the <tt>PreferenceValue</tt> interface
     * @param user an optional (nullable) user argument that can be used to convert the value holders before storing
     * @param instance an optional (nullable) instance argument that can be used to convert the value holders before storing
     * @return an updated set of <tt>PreferenceValue</tt> objects, if some value holders were converted
     */
    public Set<PreferenceValue> store(PreferenceComponent component, Set<PreferenceValue> valueHolders, Object user, Object instance);

    /**
     * Delete all preference setting for a particular user (because the user was deleted)
     * @param user the user the preference values should be deleted for
     */
    public void deleteUserPreferences(Object user);

    /**
     * Delete all preference setting for a particular instance (because the instance was deleted)
     * @param instance the oinstance the preference values should be deleted for
     */
    public void deleteInstancePreferences(Object instance);

    /**
     * Write the queued preference values to a permanent data store
     */
    public void flush();
}

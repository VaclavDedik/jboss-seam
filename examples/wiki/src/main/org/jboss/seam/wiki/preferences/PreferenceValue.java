package org.jboss.seam.wiki.preferences;

/**
 * Implementation of a value holder, load and stored by <tt>PreferenceProvider</tt>.
 * <p>
 * Use this interface to plug-in your own preference values, your <tt>PreferenceProvider</tt>
 * has to return values wrapped in this interface and it will receive values wrapped in this
 * interface.
 *
 * @author Christian Bauer
 */
public interface PreferenceValue {

    public Object getValue();
    public void setValue(Object value);

    public void setPreferenceProperty(PreferenceProperty property);
    public PreferenceProperty getPreferenceProperty();

    public boolean isDirty();

    public boolean isSystemAssigned();
    public boolean isUserAssigned();
    public boolean isInstanceAssigned();

}

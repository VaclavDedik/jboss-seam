package org.jboss.seam.wiki.preferences;

import org.jboss.seam.util.Reflections;
import org.jboss.seam.Component;
import org.hibernate.validator.InvalidValue;
import org.hibernate.validator.ClassValidator;
import org.hibernate.validator.InvalidStateException;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.io.Serializable;

/**
 * Meta model, represents a field or property of a Seam component that extends <tt>PreferenceSupport</tt> and
 * has a <tt>Preference</tt> annotation.
 *
 * @author Christian Bauer
 */
public class PreferenceProperty implements Comparable, Serializable {

    private String name;
    private String description;
    private PreferenceVisibility visibility;
    private boolean fieldAccess;
    private PreferenceComponent preferenceComponent;

    public PreferenceProperty(String name, String description, PreferenceVisibility visibility, boolean fieldAccess, PreferenceComponent preferenceComponent) {
        this.name = name;
        this.description = description;
        this.visibility = visibility;
        this.fieldAccess = fieldAccess;
        this.preferenceComponent = preferenceComponent;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public PreferenceVisibility getVisibility() {
        return visibility;
    }

    public PreferenceComponent getPreferenceComponent() {
        return preferenceComponent;
    }

    public boolean allowsUserOverride() {
        return getVisibility().equals(PreferenceVisibility.USER) || getVisibility().equals(PreferenceVisibility.INSTANCE);
    }

    public boolean allowsInstanceOverride() {
        return getVisibility().equals(PreferenceVisibility.INSTANCE);
    }

    public void write(Object componentInstance, Object value) throws Exception {
        Component component = Component.forName(Component.getComponentName(componentInstance.getClass()));

        // Validate first
        // TODO: The exception is currently swallowed... but ideally we should never have invalid input here (from user or database)
        InvalidValue[] invalidValues = validate(component, value);
        if (invalidValues.length >0)
            throw new InvalidStateException(invalidValues, component.getName() + "." + getName());

        if (fieldAccess) {
            Field field = Reflections.getField(componentInstance.getClass(), getName());
            field.setAccessible(true);
            Reflections.set(field, componentInstance, value);
        } else {
            Method setterMethod = Reflections.getSetterMethod(componentInstance.getClass(), getName());
            setterMethod.setAccessible(true);
            Reflections.invoke(setterMethod, componentInstance, value);
        }
    }

    public InvalidValue[] validate(Component component, Object value) {
        ClassValidator validator = component.getValidator();
        return validator.getPotentialInvalidValues( getName(), value );
    }

    public int compareTo(Object o) {
        return getDescription().compareTo( ((PreferenceProperty)o).getDescription() );
    }

    public String toString() {
        return "PreferenceProperty: " + getName();
    }
}

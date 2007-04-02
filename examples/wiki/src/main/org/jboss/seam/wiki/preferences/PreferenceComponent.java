package org.jboss.seam.wiki.preferences;

import org.jboss.seam.Component;
import org.hibernate.validator.InvalidValue;

import java.util.*;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.beans.Introspector;

/**
 * Meta model, represents a Seam component that extends <tt>PreferenceSupport</tt> and has a <tt>Preference</tt> annotation.
 *
 * @author Christian Bauer
 */
public class PreferenceComponent implements Comparable {

    private String name;
    private String description;
    private PreferenceVisibility visibility;
    private SortedSet<PreferenceProperty> properties = new TreeSet<PreferenceProperty>();
    private Map<String, PreferenceProperty> propertiesByName = new HashMap<String, PreferenceProperty>();

    private Component component;

    public PreferenceComponent(Component component) {

        this.component = component;
        this.name = component.getName();
        this.description = component.getBeanClass().getAnnotation(Preference.class).description();
        this.visibility = component.getBeanClass().getAnnotation(Preference.class).visibility();

        // Now find the preference properties this component wants
        Class beanClass = component.getBeanClass();

        String propertyName;
        String propertyDescription;
        PreferenceVisibility propertyVisibility;

        // @Preference setter methods
        Method[] methods = beanClass.getDeclaredMethods();
        for (Method method : methods) {
            String methodName = method.getName();
            if ( method.isAnnotationPresent(Preference.class) &&
                 methodName.startsWith("set") &&
                 method.getParameterTypes().length == 1) {
                if ( !method.isAccessible() ) method.setAccessible(true);

                propertyName = Introspector.decapitalize(methodName.substring(3));
                propertyDescription  = method.getAnnotation(Preference.class).description();
                propertyVisibility = method.getAnnotation(Preference.class).visibility();

                PreferenceProperty property =
                        new PreferenceProperty(propertyName, propertyDescription, propertyVisibility, false, this);
                properties.add(property);
                propertiesByName.put(property.getName(), property);
            }
        }

        // @Preference fields
        for ( Field field: beanClass.getDeclaredFields() ) {
            if ( field.isAnnotationPresent(Preference.class) ) {
                if ( !field.isAccessible() ) field.setAccessible(true);

                propertyName = field.getName();
                propertyDescription = field.getAnnotation(Preference.class).description();
                propertyVisibility = field.getAnnotation(Preference.class).visibility();

                PreferenceProperty property =
                        new PreferenceProperty(propertyName, propertyDescription, propertyVisibility, true, this);
                properties.add(property);
                propertiesByName.put(property.getName(), property);
            }
        }
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

    public boolean allowsUserOverride() {
        return getVisibility().equals(PreferenceVisibility.USER) || getVisibility().equals(PreferenceVisibility.INSTANCE);
    }

    public boolean allowsInstanceOverride() {
        return getVisibility().equals(PreferenceVisibility.INSTANCE);
    }

    public SortedSet<PreferenceProperty> getProperties() {
        return properties;
    }

    public SortedSet<PreferenceProperty> getProperties(PreferenceVisibility visibility) {
        SortedSet<PreferenceProperty> filteredProperties = new TreeSet<PreferenceProperty>();
        for (PreferenceProperty property : properties) {
            if (property.getVisibility().ordinal() >= visibility.ordinal()) filteredProperties.add(property);
        }
        return filteredProperties;
    }

    public Map<String, PreferenceProperty> getPropertiesByName() {
        return propertiesByName;
    }

    public Component getComponent() {
        return component;
    }

    public Map<PreferenceProperty, InvalidValue[]> validate(Collection<PreferenceValue> valueHolders) {
        Map<PreferenceProperty, InvalidValue[]> invalidProperties = new HashMap<PreferenceProperty, InvalidValue[]>();
        for (PreferenceValue valueHolder : valueHolders) {
            PreferenceProperty property = valueHolder.getPreferenceProperty();
            invalidProperties.put(property, property.validate(getComponent(), valueHolder.getValue()));
        }
        return invalidProperties;
    }

    public int compareTo(Object o) {
        return getDescription().compareTo( ((PreferenceComponent)o).getDescription() );
    }

}

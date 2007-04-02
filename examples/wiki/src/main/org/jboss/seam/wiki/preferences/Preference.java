package org.jboss.seam.wiki.preferences;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;

/**
 * Used for definition of preferences meta model.
 * <p>
 * Put this annotation on either a Seam component that extends <tt>PreferenceSupport</tt> or on a
 * field of that component. You can then access the preference setting via
 * <tt>#{prefComponent.properties['prefProperty']}</tt> or with <tt>#{prefComponent.prefProperty}</tt>
 * if you have defined a getter method. You can also put this annotation on the setter method instead
 * of the field. Use Hibernate Validator annotations on the property fields or getters to restrict
 * the applicable value ranges.
 * <p>
 * The <tt>PreferenceRegistry</tt> reads these annotations and builds the internal meta model for
 * preference loading, saving, and editing.
 * <p>
 * Note that all preference components and properties that have <tt>USER</tt> visibility also automatically
 * have <tt>INSTANCE</tt> visibility. That means you can not define a preference component or property that
 * has only <tt>INSTANCE</tt> visibility and that can not be configured on a user-level.
 * 
 * @author Christian Bauer
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Preference {
    String description();
    PreferenceVisibility visibility() default PreferenceVisibility.SYSTEM;
}

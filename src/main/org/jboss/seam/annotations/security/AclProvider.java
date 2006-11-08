package org.jboss.seam.annotations.security;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Used in conjunction with @DefinePermissions to specify which AclProvider
 * should be used to check permissions for the specified actions against the object.
 *
 * @author Shane Bryzak
 */
@Target(ANNOTATION_TYPE)
@Retention(RUNTIME)
public @interface AclProvider
{
  String action();
  String provider();
  int mask() default 0;
}

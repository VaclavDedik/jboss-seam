package org.jboss.seam.annotations;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Used in conjunction with @PermissionDefs
 *
 * @author Shane Bryzak
 */
@Target(ANNOTATION_TYPE)
@Retention(RUNTIME)
public @interface PermissionProvider
{
  String actions();
  String provider();
}

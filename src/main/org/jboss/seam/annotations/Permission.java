package org.jboss.seam.annotations;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Used in conjunction with @Secure to control access to a Seam component or
 * component method.
 *
 * @author Shane Bryzak
 */
@Target(ANNOTATION_TYPE)
@Retention(RUNTIME)
public @interface Permission
{
  String object();
  String action();
}

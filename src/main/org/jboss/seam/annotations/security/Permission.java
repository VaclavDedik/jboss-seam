package org.jboss.seam.annotations.security;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Used to define a specific permission for a Seam component
 *
 * @author Shane Bryzak
 */
@Target(ANNOTATION_TYPE)
@Retention(RUNTIME)
public @interface Permission
{
  String action();
  String expr();
}

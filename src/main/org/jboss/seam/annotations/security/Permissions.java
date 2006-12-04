package org.jboss.seam.annotations.security;

/**
 *
 *
 * @author Shane Bryzak
 */
public @interface Permissions
{
  Permission[] value() default {};
}

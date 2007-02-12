package org.jboss.seam.annotations.security;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Used to restrict access to a Seam component or component method.
 *
 * @author Shane Bryzak
 */
@Target({TYPE,METHOD})
@Documented
@Retention(RUNTIME)
public @interface Restrict 
{
   /**
    * Restrictions may be expressed using any EL expression, and usually
    * include the use of s:hasRole(...) or s:hasPermission(..., /..).
    * 
    * @return An EL expression that defines the restriction to be checked
    */
   String value() default "";
}

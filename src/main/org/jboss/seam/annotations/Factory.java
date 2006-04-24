//$Id$
package org.jboss.seam.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks a method as a factory method for a context variable,
 * meaning it is responsible for outjecting a value for the
 * named context variable when no value is bound to the
 * variable. Supports use of the Seam "factory component"
 * pattern.
 * 
 * @author Gavin King
 */
@Target(METHOD)
@Retention(RUNTIME)
@Documented
public @interface Factory {
   /**
    * @return the name of the context variable
    */
   String value() default "";
}

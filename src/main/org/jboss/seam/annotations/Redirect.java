package org.jboss.seam.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies that an exception should result in a 
 * browser redirect.
 * 
 * @author Gavin King
 *
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
public @interface Redirect
{
   /**
    * The message to be displayed as a FacesMessage, default
    * to using the exception message.
    * 
    * @return a templated message
    */
   String message() default "";
   /**
    * The view to redirect to, default to the current view.
    * 
    * @return a JSF view id
    */
   String viewId();
}

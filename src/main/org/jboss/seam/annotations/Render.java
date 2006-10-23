package org.jboss.seam.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies that an exception should result in immediate
 * rendering of the view. This may only be used with 
 * exceptions thrown during the INVOKE_APPLICATION phase.
 * 
 * @author Gavin King
 *
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
@Inherited
public @interface Render
{
   /**
    * The message to be displayed as a FacesMessage, default
    * to using the exception message.
    * 
    * @return a templated message
    */
   String message() default "";
   /**
    * The view to render, default to the current view.
    * 
    * @return a JSF view id
    */
   String viewId() default "";
   /**
    * Should the current long-running conversation end
    * when this exception occurs.
    * 
    * @return true if we should end the conversation
    */
   boolean end() default false;
}

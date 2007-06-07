package org.jboss.seam.annotations;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * A parameter-level annotation to identify a method parameter to use as the
 * conversation ID.  Used for conversation management within web services.
 * 
 * @author Shane Bryzak
 */
@Target(PARAMETER)
@Retention(RUNTIME)
@Documented
public @interface ConversationId
{
   /**
    * An EL expression which can be used to extract the conversation ID from
    * the object graph of the parameter, if the parameter is not a simple type
    * and does not represent the conversation ID itself.
    */
   String value() default "";
}

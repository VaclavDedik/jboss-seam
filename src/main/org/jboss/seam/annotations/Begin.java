//$Id$
package org.jboss.seam.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks a method as beginning a conversation, if none
 * exists, and if the method returns without throwing 
 * an exception. If a list of outcomes is specified,
 * the conversation begins only if the outcome is in
 * the list.
 * 
 * @author Gavin King
 */
@Target(METHOD)
@Retention(RUNTIME)
@Documented
public @interface Begin {
	/**
	 * An empty outcome list is interpreted to mean any 
	 * outcome except for the null (redisplay) outcome.
	 */
	String[] ifOutcome() default {};
   /**
    * If enabled, and if a conversation is already active,
    * begin a nested conversation, instead of continuing
    * in the context of the existing conversation. 
    */
   boolean nested() default false;
   /**
    * If false (the default), invocation of the begin
    * method in the scope of an existing conversation
    * will cause an exception to be thrown.
    */
   boolean join() default false;
   /**
    * The name of the jBPM process definition defining 
    * the page flow for this conversation.
    */
   String processDefinition() default "";
}

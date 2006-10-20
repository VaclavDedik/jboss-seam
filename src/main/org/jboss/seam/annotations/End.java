//$Id$
package org.jboss.seam.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks a method as ending a conversation, if the
 * method returns a non-null outcome without throwing 
 * an exception. If a list of outcomes is specified, 
 * the conversation ends only if the outcome is in 
 * the list. A null outcome never ends the conversation.
 * If the method is of type void, the conversation always
 * ends.
 * 
 * @author Gavin King
 */
@Target(METHOD)
@Retention(RUNTIME)
@Documented
public @interface End {
	/**
	 * An empty outcome list is interpreted to mean any 
	 * outcome except for the null (redisplay) outcome.
	 */
	String[] ifOutcome() default {};
   /**
    * Should the conversation be destroyed before any
    * redirect? (The default behavior is to propagate
    * the conversation across the redirect and then
    * destroy it at the end of the redirected request.)
    * 
    * @return false by default
    */
   boolean beforeRedirect() default false;
}

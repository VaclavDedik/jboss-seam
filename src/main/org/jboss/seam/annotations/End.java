//$Id$
package org.jboss.seam.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks a method as ending a conversation, if the
 * method returns without throwing an exception. If 
 * a list of outcomes is specified, the conversation 
 * ends only if the outcome is in the list. A null
 * outcome never ends the conversation.
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
}

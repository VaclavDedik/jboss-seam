//$Id$
package org.jboss.seam.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(METHOD)
@Retention(RUNTIME)
@Documented
/**
 * Marks a method as conditionally ending a conversation. 
 * If result is specified, the conversation ends only when 
 * the method returns one of the listed string values. If
 * exception is specified, the conversation returns
 * only if the method throws one of the listed classes
 * of exception.
 * 
 * @author Gavin King
 */
public @interface EndConversationIf {
	String[] result() default {};
	Class[] exception() default {};
}

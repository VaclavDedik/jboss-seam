//$Id$
package org.jboss.seam.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks any method as causing a transaction
 * rollback. If the method is an action listener
 * method and a list of outcomes is specified,
 * the transaction is rolled back only if the 
 * outcome is in the list.
 * 
 * @author Gavin King
 */
@Target(METHOD)
@Retention(RUNTIME)
@Documented
public @interface Rollback {
	/**
	 * An empty outcome list is interpreted to mean any 
	 * outcome.
	 */
	String[] ifOutcome() default {};

}

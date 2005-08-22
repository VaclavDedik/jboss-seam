//$Id$
package org.jboss.seam.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies that a component is conversational, and
 * that its methods may only be called inside the 
 * scope of a long-running conversation that was
 * begun by a call to the component's @Begin method.
 * 
 * @author Gavin King
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
public @interface Conversational
{
   String ifNotBegunOutcome();
}

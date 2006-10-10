//$Id$
package org.jboss.seam.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies that the object receiving an action method
 * invocation should be validated before the call and,
 * if in an invalid state, the call will be aborted and
 * result in the specified outcome. The array of
 * InvalidValues will be bound to the request context
 * with the specified name.
 * 
 * @author Gavin King
 * 
 * @deprecated use <s:validate/> or <s:validateAll/>
 * 
 */
@Target(METHOD)
@Retention(RUNTIME)
@Documented
public @interface IfInvalid
{
   /**
    * The JSF outcome, in the case that the reciever is
    * in an invalid state.
    */
   String outcome();
   /**
    * Should we refresh any entity instance that violates
    * its constraints?
    */
   boolean refreshEntities() default false;
   /**
    * The name of a Seam-managed persistence context to
    * use for refreshing entities when contstraints are
    * violated.
    */
   String persistenceContext() default "entityManager";
}

//$Id$
package org.jboss.seam.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies that the object receiving an action method
 * invocation should be validated before the call and,
 * if in an invalid state, the call will be aborted and
 * result in the specified outcome.
 * @author Gavin King
 */
@Target({FIELD, METHOD, PARAMETER})
@Retention(RUNTIME)
@Documented
public @interface IfInvalid
{
    String outcome();
}

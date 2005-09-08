//$Id$
package org.jboss.seam.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.jboss.seam.InterceptionType;

/**
 * Specifies the interception type of a Seam component.
 * 
 * @author Gavin King
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
public @interface Intercept
{
   InterceptionType value();
}

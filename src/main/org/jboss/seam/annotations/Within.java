//$Id$
package org.jboss.seam.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies that an interceptor is called "within" 
 * another interceptor or interceptors.
 * 
 * @author Gavin King
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
public @interface Within
{
   Class[] value();
}

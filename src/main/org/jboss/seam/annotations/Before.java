//$Id$
package org.jboss.seam.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies that a beforeInvoke or afterReturn method
 * is called before the corresponding method on some 
 * other interceptor class.
 * 
 * @author Gavin King
 */
@Target(METHOD)
@Retention(RUNTIME)
@Documented
public @interface Before
{
   Class[] value();
}

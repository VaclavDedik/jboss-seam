//$Id$
package org.jboss.seam.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies that the object returned by the annotated
 * getter method is to be injected instead of the
 * component itself.
 * 
 * @author Gavin King
 */
@Target(METHOD)
@Retention(RUNTIME)
@Documented
public @interface Unwrap
{

}

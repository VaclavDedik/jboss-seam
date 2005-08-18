//$Id$
package org.jboss.seam.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Meta-annotation that declares the seam interceptor
 * associated with an annotation.
 * 
 * @author Gavin King
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
public @interface Advice
{
   public Class value();
}

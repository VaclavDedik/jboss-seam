//$Id$
package org.jboss.seam.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies that an instance of this component is
 * created at system initialization time. May only
 * be applied to APPLICATION scoped components.
 * 
 * @author Gavin King
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
public @interface Startup {}

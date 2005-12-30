//$Id$
package org.jboss.seam.annotations;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Injects a request parameter value
 * 
 * @author Gavin King
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
@Documented
public @interface RequestParameter {
   /**
    * The name of the request parameter
    */
   String value() default "";
}

//$Id$
package org.jboss.seam.annotations;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Injects a log
 * 
 * @author Gavin King
 */
@Target(FIELD)
@Retention(RUNTIME)
@Documented
public @interface ComponentLog {
   /**
    * @return the log category
    */
   String value() default "";
}

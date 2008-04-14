package org.jboss.seam.annotations.security;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 
 *
 * @author Shane Bryzak
 */
@Target({METHOD})
@Documented
@Retention(RUNTIME)
@Inherited
@PermissionCheck("insert")
public @interface Insert {
   Class value();
}

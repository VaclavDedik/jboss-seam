package org.jboss.seam.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Used to explicitly define permissions for a component
 *
 * @author Shane Bryzak
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
public @interface DefinePermissions
{
  String name() default "";
  PermissionProvider[] permissions() default {};
}

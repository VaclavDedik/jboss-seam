package org.jboss.seam.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Used to configure security for a Seam component or component method.
 *
 * @author Shane Bryzak
 */
@Target({TYPE,METHOD})
@Documented
@Retention(RUNTIME)
public @interface Secure
{
  Permission[] permissions() default {};
  String[] roles() default "";
  String onfail() default "";
}

package org.jboss.seam.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Constrains an outgoing object graph returned by a remoting invocation.
 *
 * @author Shane Bryzak
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface Constrain
{
  /**
   *
   */
  String[] value() default {};
}

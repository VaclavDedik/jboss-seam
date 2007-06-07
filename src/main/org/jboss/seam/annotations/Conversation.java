package org.jboss.seam.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Used to configure a named conversation for a web service.
 * 
 * @author Shane Bryzak
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
public @interface Conversation
{
   String value() default "";
}

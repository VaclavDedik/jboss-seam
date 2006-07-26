package org.jboss.seam.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.jboss.seam.InterceptorType;

/**
 * Annotates an interceptor class and specifies what 
 * kind of interceptor it is (client side or server 
 * side).
 * 
 * @author Gavin King
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
public @interface Interceptor
{
   InterceptorType type() default InterceptorType.SERVER;
}

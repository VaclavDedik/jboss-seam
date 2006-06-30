package org.jboss.seam.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.servlet.http.HttpServletResponse;

@Target(TYPE)
@Retention(RUNTIME)
@Documented
public @interface HttpError
{
   int errorCode() default HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
}

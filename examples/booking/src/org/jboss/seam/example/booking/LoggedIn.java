//$Id$
package org.jboss.seam.example.booking;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.jboss.seam.annotations.Advice;

@Target(TYPE)
@Retention(RUNTIME)
@Documented
@Advice(LoggedInInterceptor.class)
public @interface LoggedIn {}

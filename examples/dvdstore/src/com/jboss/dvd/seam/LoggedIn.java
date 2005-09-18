package com.jboss.dvd.seam;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.ejb.Interceptor;

@Target(TYPE)
@Retention(RUNTIME)
@Documented
@Interceptor(LoggedInInterceptor.class)
public @interface LoggedIn {
}


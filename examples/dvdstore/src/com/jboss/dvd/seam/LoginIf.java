package com.jboss.dvd.seam;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.ejb.Interceptor;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Interceptor(LoginIfInterceptor.class)
public @interface LoginIf {
    String[] outcome() default {};
}


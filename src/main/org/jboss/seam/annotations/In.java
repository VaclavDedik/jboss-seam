/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.jboss.seam.ScopeType;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Specifies that a seam component should be injected to
 * the annotated field or setter method of a seam component.
 * 
 * @author Gavin King
 */
@Target({FIELD, METHOD})
@Retention(RUNTIME)
@Documented
public @interface In {
   /**
    * The context variable name. Defaults to the name of 
    * the annotated field or getter method.
    */
	String value() default "";
   /**
    * Specifies that a component should be instantiated
    * if the context variable is null.
    */
   boolean create() default false;
   /**
    * Specifies that the injected value must not be
    * null, by default.
    */
   boolean required() default true;
   /**
    * Explicitly specify the scope to search, instead of
    * searching all scopes.
    */
   ScopeType scope() default ScopeType.UNSPECIFIED;
}

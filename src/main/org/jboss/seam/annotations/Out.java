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
 * Specifies that a seam component should be outjected from
 * the annotated field or setter method of a session bean.
 * 
 * @author Gavin King
 */
@Target({FIELD, METHOD})
@Retention(RUNTIME)
@Documented
public @interface Out {
   /**
    * The context variable name. Defaults to the name of 
    * the annotated field or getter method.
    */
	String value() default "";
   /**
    * Specifies that the outjected value must not be
    * null, by default.
    */
   boolean required() default true;
   /**
    * Specifies the scope, for values which are not
    * instances of a Seam component.
    */
   ScopeType scope() default ScopeType.UNSPECIFIED;
}

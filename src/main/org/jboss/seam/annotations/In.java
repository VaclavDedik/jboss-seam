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
 * the annotated field or setter method of a seam component 
 * May also be used to inject a jBPM ProcessInstance or an 
 * instance of java.util.Properties.
 * 
 * @author Gavin King
 */
@Target({FIELD, METHOD})
@Retention(RUNTIME)
@Documented
public @interface In {
	String value() default "";
   boolean create() default false;
   boolean required() default true;
   ScopeType scope() default ScopeType.UNSPECIFIED;
}

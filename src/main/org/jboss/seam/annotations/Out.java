/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Specifies that a seam component should be outjected from
 * the annotated field or setter method of a session 
 * bean.
 * 
 * @author Gavin King
 */
@Target({FIELD, METHOD})
@Retention(RUNTIME)
public @interface Out {
	String value() default "";
}

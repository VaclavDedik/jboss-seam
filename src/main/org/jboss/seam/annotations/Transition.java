/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks a method as causing a jBPM transition, unless the
 * method returns the null outcome.
 */
@Target(METHOD)
@Retention(RUNTIME)
@Documented
public @interface Transition {
   
   /**
    * @return the transition name
    */
   String value();
   
}

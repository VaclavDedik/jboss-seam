/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the configuration namespace of a seam component.
 * 
 * @author Norman Richards
 */
@Target(ElementType.PACKAGE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Namespace 
{
    /**
     * @return the configuration namespace
     */
    String value();
    /**
     *  The component package name prefix, if any
     */
    String prefix() default "";
}



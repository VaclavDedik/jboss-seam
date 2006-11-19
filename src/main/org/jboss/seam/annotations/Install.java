/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.annotations;

import java.lang.annotation.*;

/**
 * Specifies whether or not a component should be installed if it is scanned
 * 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Install {
    /**
     * @return indicates if the component should be installed
     */
    boolean value() default true;
    
    /**
     *  indicates that the component should not be installed unless the
     *  dependent components are installed
     */
    String[] dependencies() default {};
    Class[] genericDependencies() default {};
}



/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.jboss.seam.ScopeType;

/**
 * Specifies the name and scope role for a seam component role.
 * If a component has no @Role annotation missing, it component 
 * has exactly one role, defined by @Name and @Scope. If it has
 * one or more @Role annotations, the component has one or more
 * roles in addition to the default role defined by @Name and 
 * @Scope.
 *
 * @author César Izurieta
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
public @interface Role {
   String name();
   ScopeType scope() default ScopeType.UNSPECIFIED;
}



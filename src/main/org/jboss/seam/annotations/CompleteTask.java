/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.Documented;

/**
 * Marks a method as causing jBPM {@link org.jbpm.taskmgmt.exe.TaskInstance task}
 * to be completed.
 *
 * @see org.jbpm.taskmgmt.exe.TaskInstance#end
 */
@Target(METHOD)
@Retention(RUNTIME)
@Documented
public @interface CompleteTask {
	/**
	 * The name of the context variable under which we should locate the
	 * the id of the task to be completed.
	 */
	String name();

	/**
	 * An array of result to transition mappings in the form "methodResult=>transitionName".
	 * No match means that the default transition will be attempted.
	 */
	String[] transitionMap() default "";
}

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

import org.jboss.seam.ScopeType;

/**
 * Marks a method as causing jBPM {@link org.jbpm.taskmgmt.exe.TaskInstance task}
 * to be completed.
 *
 * @see org.jbpm.taskmgmt.exe.TaskInstance#end
 *
 * TODO : need to figure a scheme for handling the transitionName;
 * possibilities:
 *      1) another context lookup.
 *      2) A mapping between the annotated method's return value to the
 *          transitionName (based on assumption this is attached to JSF
 *          action handler returning the logic name of the page to go
 *          to as a result)
 *      3) (same assumption) require the trasitionName and action result
 *          name be the same
 */
@Target(METHOD)
@Retention(RUNTIME)
@Documented
public @interface CompleteTask {
	/**
	 * The name of the context variable under which we should locate the
	 * the task to be completed.
	 */
	String contextName();
	/**
	 * The scope in which the said variable is contained.
	 * TODO : or should we just use Contexts.lookupInStatefulContexts()?
	 */
	ScopeType contextScope() default ScopeType.CONVERSATION;
}

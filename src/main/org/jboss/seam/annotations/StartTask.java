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
 * to be marked as started.
 *
 * @see org.jbpm.taskmgmt.exe.TaskInstance#start()
 *
 * TODO : do we need to handle setActorId(String) or start(String)?
 */
@Target(METHOD)
@Retention(RUNTIME)
@Documented
public @interface StartTask {
	/**
	 * The name of the context variable under which we should locate the
	 * the task to be started.
	 */
	String contextName();
	/**
	 * The scope in which the said variable is contained.
	 * TODO : or should we just use Contexts.lookupInStatefulContexts()?
	 */
	ScopeType contextScope() default ScopeType.CONVERSATION;
}

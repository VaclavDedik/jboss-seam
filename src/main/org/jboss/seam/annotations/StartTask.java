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
	 * the id of task to be started.
	 */
	String contextName();
}

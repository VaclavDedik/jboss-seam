package org.jboss.seam.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.jboss.seam.ScopeType;

/**
 * Marks a method as causing jBPM {@link org.jbpm.taskmgmt.exe.TaskInstance task}
 * to be cancelled.
 *
 * @see org.jbpm.taskmgmt.exe.TaskInstance#cancel()
 */
@Target(METHOD)
@Retention(RUNTIME)
@Documented
public @interface CancelTask {
	/**
	 * The name of the context variable under which we should locate the
	 * the task to be cancelled.
	 */
	String contextName();
	/**
	 * The scope in which the said variable is contained.
	 * TODO : or should we just use Contexts.lookupInStatefulContexts()?
	 */
	ScopeType contextScope() default ScopeType.CONVERSATION;
}

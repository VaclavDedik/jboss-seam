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
 * Marks a method as causing jBPM {@link org.jbpm.taskmgmt.exe.TaskInstance task}
 * to be marked as started.
 * <p/>
 * Note that both {@link BeginTask} and {@link StartTask} have effect
 * before invocation of the intercepted method in that they are both
 * about setting up appropriate {@link org.jbpm.context.exe.ContextInstance}
 * for the current {@link org.jboss.seam.contexts.BusinessProcessContext};
 * {@link StartTask} however, also has effect after method invocation
 * as that is the time it actually marks the task as started.
 *
 * @see org.jbpm.taskmgmt.exe.TaskInstance#start()
 */
@Target( METHOD )
@Retention( RUNTIME )
@Documented
public @interface StartTask
{
   /**
    * The name of the request parameter under which we should locate the
    * the id of task to be started.
    */
   String taskIdParameter() default "taskId";
   /**
    * Should we push actor information onto the task, or allow any defined
    * assigments/swimlanes take effect?
    */
   boolean pushActor() default false;
}

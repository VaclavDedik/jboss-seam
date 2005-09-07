/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.annotations;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.Documented;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.METHOD;

/**
 * Marks a method as causing jBPM {@link org.jbpm.taskmgmt.exe.TaskInstance task}
 * to be resumed.  Essentially this simply re-associates the jBPM
 * {@link org.jbpm.context.exe.ContextInstance} with the current
 * {@link org.jboss.seam.contexts.BusinessProcessContext}.
 * <p/>
 * Note that both {@link ResumeTask} and {@link StartTask} have effect
 * before invocation of the intercepted method in that they are both
 * about setting up appropriate {@link org.jbpm.context.exe.ContextInstance}
 * for the current {@link org.jboss.seam.contexts.BusinessProcessContext}.
 *
 */
@Target( METHOD )
@Retention( RUNTIME )
@Documented
public @interface ResumeTask
{
   /**
    * The name of the context variable under which we should locate the
    * the id of task to be resumed.
    */
   String taskIdName();
   /**
    * The name under which to expose the jBPM
    * {@link org.jbpm.taskmgmt.exe.TaskInstance} into conversation context.
    *
    * optional; defaults to {@link org.jboss.seam.interceptors.BusinessProcessInterceptor#DEF_TASK_NAME}.
    */
   String taskName() default "";
   /**
    * The name under which to expose the jBPM
    * {@link org.jbpm.graph.exe.ProcessInstance} into conversation context.
    *
    * optional; defaults to {@link org.jboss.seam.interceptors.BusinessProcessInterceptor#DEF_PROCESS_NAME}.
    */
   String processName() default "";
}

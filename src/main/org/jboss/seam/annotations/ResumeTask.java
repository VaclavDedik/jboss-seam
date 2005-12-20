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
    * The name of the request parameter under which we should locate the
    * the id of task to be resumed.
    */
   String taskIdParameter() default "taskId";
 }

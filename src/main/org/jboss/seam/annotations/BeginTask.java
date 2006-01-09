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
 * to be resumed. The jBPM {@link org.jbpm.context.exe.ContextInstance} 
 * is associated with the BUSINESS_PROCESS scope and the 
 * {@link org.jbpm.taskmgmt.exe.TaskInstance} is associated with a new
 * conversation.
 * <p/>
 * Note that both {@link BeginTask} and {@link StartTask} have effect
 * before invocation of the intercepted method in that they are both
 * about setting up appropriate {@link org.jbpm.context.exe.ContextInstance}
 * for the current {@link org.jboss.seam.contexts.BusinessProcessContext}.
 *
 */
@Target( METHOD )
@Retention( RUNTIME )
@Documented
public @interface BeginTask
{
   /**
    * The name of the request parameter under which we should locate the
    * the id of task to be resumed.
    */
   String taskIdParameter() default "taskId";
   /**
    * The name of the jBPM process definition defining 
    * the page flow for this conversation.
    */
   String pageflow() default "";
}

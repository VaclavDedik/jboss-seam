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

import org.jboss.seam.interceptors.BusinessProcessInterceptor;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.METHOD;

/**
 * Marks a method as causing jBPM {@link org.jbpm.graph.exe.ProcessInstance process}
 * to be resumed.  Essentially this simply re-associates the jBPM
 * {@link org.jbpm.context.exe.ContextInstance} with the current
 * {@link org.jboss.seam.contexts.BusinessProcessContext}.
 */
@Target( METHOD )
@Retention( RUNTIME )
@Documented
public @interface ResumeProcess
{
   /**
    * The name of the request parameter under which we should locate the
    * the id of process to be resumed.
    */
   String processIdParameter() default "jbpmProcessId";
   /**
    * The name under which to expose the jBPM
    * {@link org.jbpm.graph.exe.ProcessInstance} into conversation context.
    *
    * optional; defaults to {@link org.jboss.seam.interceptors.BusinessProcessInterceptor#DEF_PROCESS_INSTANCE_NAME}.
    */
   String processName() default BusinessProcessInterceptor.DEF_PROCESS_INSTANCE_NAME;
}

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
   String processIdParameter() default "processId";
}

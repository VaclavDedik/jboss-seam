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
 * to be ended. The current conversation also ends. If a list of outcomes 
 * is specified, the task ends only if the outcome is in the list. A null
 * outcome never ends the task.
 *
 * @see org.jbpm.taskmgmt.exe.TaskInstance#end
 */
@Target(METHOD)
@Retention(RUNTIME)
@Documented
public @interface EndTask {
   
   /**
    * An empty outcome list is interpreted to mean any 
    * outcome except for the null (redisplay) outcome.
    */
   String[] ifOutcome() default {};
   
   /**
    * Specifies the transition that should be triggered by
    * completing the task. If the transition needs to be
    * specified dynamically, use the Seam <tt>transition</tt>
    * component, calling <tt>Transition.setName()<tt>.
    * 
    * @return a transition name
    */
   String transition() default "";
   
}

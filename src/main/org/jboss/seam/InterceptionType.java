//$Id$
package org.jboss.seam;

import javax.faces.event.PhaseId;

import org.jboss.seam.contexts.Lifecycle;

/**
 * Rules for when Seam will intercept invocations upon a
 * component to perform bijection, validation, context
 * demarcation, etc. For JavaBeans and session beans, the 
 * default interception type is <tt>INVOKE_APPLICATION</tt>.
 * All entity beans have interception type <tt>NEVER</tt>.
 * 
 * @author Gavin King
 */
public enum InterceptionType
{   
   /**
    * Seam never intercepts invocations upon the component
    * (default for entity bean components)
    */
   NEVER,
   /**
    * Seam intercepts any invocations that occur after the 
    * restore view phase (default for session bean and
    * JavaBean components)
    */
   AFTER_RESTORE_VIEW,
   /**
    * Seam intercepts invocations upon the component only during
    * the invoke application phase
    */
   INVOKE_APPLICATION,
   /**
    * Seam always intercepts invocations upon the component
    */
   ALWAYS;

   public boolean isActive()
   {
      final PhaseId phaseId = Lifecycle.getPhaseId();
      switch(this)
      {
         case NEVER:
            return false;
         case AFTER_RESTORE_VIEW:
            return phaseId!=PhaseId.RESTORE_VIEW && phaseId!=null;
         case INVOKE_APPLICATION:
            return phaseId==PhaseId.INVOKE_APPLICATION;
         case ALWAYS:
            return true;
         default:
            throw new IllegalArgumentException();
      }
   }
}

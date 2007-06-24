package org.jboss.seam.async;

import java.io.Serializable;

import org.jboss.seam.bpm.BusinessProcess;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.Init;

/**
 * Something that happens asynchronously, and with a full
 * set of Seam contexts, including propagation of the 
 * business process and task instances.
 * 
 * @author Gavin King
 *
 */
public abstract class Asynchronous implements Serializable
{
   static final long serialVersionUID = -551286304424595765L;
   
   private Long processId;
   private Long taskId;
   
   protected Asynchronous()
   {
      if ( Init.instance().isJbpmInstalled() )
      {
         BusinessProcess businessProcess = BusinessProcess.instance();
         processId = businessProcess.getProcessId();
         taskId = BusinessProcess.instance().getTaskId();
      }        
   }
   
   public void execute(Object timer)
   {
      boolean createContexts = !Contexts.isEventContextActive() && !Contexts.isApplicationContextActive();
      if (createContexts) Lifecycle.beginCall();
      Contexts.getEventContext().set(AbstractDispatcher.EXECUTING_ASYNCHRONOUS_CALL, true);
      try
      {
         executeInContexts(timer);         
      }
      finally
      {
         Contexts.getEventContext().remove(AbstractDispatcher.EXECUTING_ASYNCHRONOUS_CALL);
         if (createContexts) Lifecycle.endCall();
      }
      
   }

   private void executeInContexts(Object timer)
   {
      if (taskId!=null)
      {
         BusinessProcess.instance().resumeTask(taskId);
      }
      else if (processId!=null)
      {
         BusinessProcess.instance().resumeProcess(processId);
      }
      
      if (timer!=null)
      {
         Contexts.getEventContext().set("timer", timer);
      }
    
      call();
   }
   
   protected abstract void call();
}
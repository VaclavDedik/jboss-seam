package org.jboss.seam.async;

import java.io.Serializable;

import org.jboss.seam.bpm.BusinessProcess;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.Init;

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
      
      //TODO: shouldn't this take place in a Seam context anyway??!? (bug in EJB3?)
      
      Lifecycle.beginCall();
      Contexts.getEventContext().set(AbstractDispatcher.EXECUTING_ASYNCHRONOUS_CALL, true);
      try
      {
         if (taskId!=null)
         {
            BusinessProcess.instance().resumeTask(taskId);
         }
         else if (processId!=null)
         {
            BusinessProcess.instance().resumeProcess(processId);
         }
         
         Contexts.getEventContext().set("timer", timer);
      
         call();
         
      }
      finally
      {
         Contexts.getEventContext().remove(AbstractDispatcher.EXECUTING_ASYNCHRONOUS_CALL);
         Lifecycle.endCall();
      }
      
   }
   
   protected abstract void call();
}
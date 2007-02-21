/**
 * 
 */
package org.jboss.seam.pages;

import org.jboss.seam.core.BusinessProcess;
import org.jboss.seam.core.Expressions.ValueBinding;

public class TaskControl
{

   private boolean isBeginTask;

   private boolean isStartTask;

   private boolean isEndTask;

   private ValueBinding<String> taskId;

   private ValueBinding<String> transition;

   public void beginOrEndTask()
   {
      if (endTask())
      {
         String t = null;
         if (transition != null) 
         {
           t =transition.getValue();
         } 
         BusinessProcess.instance().validateTask();
         BusinessProcess.instance().endTask(t);
      }
      if (beginTask() || startTask())
      {
         BusinessProcess.instance().resumeTask(new Long(taskId.getValue()));
      }
      if (startTask())
      {
         BusinessProcess.instance().startTask();
      }
   }

   private boolean beginTask()
   {
      return isBeginTask && taskId.getValue() != null;
   }

   private boolean startTask()
   {
      return isStartTask && taskId.getValue() != null;
   }

   private boolean endTask()
   {
      return isEndTask;
   }

   public boolean isBeginTask()
   {
      return isBeginTask;
   }

   public void setBeginTask(boolean isBeginTask)
   {
      this.isBeginTask = isBeginTask;
   }

   public boolean isEndTask()
   {
      return isEndTask;
   }

   public void setEndTask(boolean isEndTask)
   {
      this.isEndTask = isEndTask;
   }

   public boolean isStartTask()
   {
      return isStartTask;
   }

   public void setStartTask(boolean isStartTask)
   {
      this.isStartTask = isStartTask;
   }

   public void setTaskId(ValueBinding<String> taskId)
   {
      this.taskId = taskId;
   }

   public ValueBinding<String> getTaskId()
   {
      return taskId;
   }
   
   public ValueBinding<String> getTransition()
   {
      return transition;
   }
   
   public void setTransition(ValueBinding<String> transition)
   {
      this.transition = transition;
   }

}
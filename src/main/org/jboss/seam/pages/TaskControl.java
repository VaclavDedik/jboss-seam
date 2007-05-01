/**
 * 
 */
package org.jboss.seam.pages;

import org.jboss.seam.core.BusinessProcess;
import org.jboss.seam.core.Expressions.ValueExpression;

public class TaskControl
{

   private boolean isBeginTask;

   private boolean isStartTask;

   private boolean isEndTask;

   private ValueExpression taskId;

   private String transition;

   public void beginOrEndTask()
   {
      if ( endTask() )
      {
         BusinessProcess.instance().validateTask();
         BusinessProcess.instance().endTask(transition);
      }
      if ( beginTask() || startTask() )
      {
         Object id = taskId.getValue();
         if (id==null)
         {
            throw new IllegalStateException("task id may not be null");
         }
         Long taskId;
         if ( id instanceof Long )
         {
            taskId = (Long) id;
         }
         else if (id instanceof String) 
         {
            taskId = new Long( (String) id );
         }
         else
         {
            throw new IllegalArgumentException("task id must be a string or long");
         }
         BusinessProcess.instance().resumeTask(taskId);
      }
      if ( startTask() )
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

   public void setTaskId(ValueExpression<String> taskId)
   {
      this.taskId = taskId;
   }

   public ValueExpression<String> getTaskId()
   {
      return taskId;
   }
   
   public String getTransition()
   {
      return transition;
   }
   
   public void setTransition(String transition)
   {
      this.transition = transition;
   }

}
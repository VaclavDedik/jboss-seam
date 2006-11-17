/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.core;

import org.jboss.seam.Component;
import org.jboss.seam.InterceptionType;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.util.Work;

/**
 * A Seam component that allows injection of the current
 * jBPM TaskInstance.
 * 
 * @author Gavin King
 * @version $Revision$
 */
@Scope(ScopeType.APPLICATION)
@Name("org.jboss.seam.core.taskInstance")
@Intercept(InterceptionType.NEVER)
public class TaskInstance 
{
   
   @Unwrap
   public org.jbpm.taskmgmt.exe.TaskInstance getTaskInstance() throws Exception
   {
      if ( !Contexts.isConversationContextActive() ) return null;
      
      return new Work<org.jbpm.taskmgmt.exe.TaskInstance>()
      {
         
         @Override
         protected org.jbpm.taskmgmt.exe.TaskInstance work() throws Exception
         {         
            Long taskId = BusinessProcess.instance().getTaskId();
            if (taskId!=null)
            {
               //TODO: do we need to cache this??
               //TODO: use getTaskInstance(), which returns null!
               return ManagedJbpmContext.instance().getTaskMgmtSession().loadTaskInstance(taskId);
            }
            else
            {
               return null;
            }
         }
         
      }.workInTransaction();
   }
   
   public static org.jbpm.taskmgmt.exe.TaskInstance instance()
   {
      if ( !Contexts.isConversationContextActive() || !BusinessProcess.instance().hasCurrentTask() ) return null; //so we don't start a txn
      
      if ( !Contexts.isApplicationContextActive() )
      {
         throw new IllegalStateException("No active application context");
      }
      return (org.jbpm.taskmgmt.exe.TaskInstance) Component.getInstance(TaskInstance.class, ScopeType.APPLICATION);
   }
   
}

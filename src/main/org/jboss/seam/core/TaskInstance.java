/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.contexts.Contexts;

/**
 * @author Gavin King
 * @version $Revision$
 */
@Scope(ScopeType.APPLICATION)
@Name("taskInstance")
@Intercept(NEVER)
@Startup
public class TaskInstance 
{
   
   @Unwrap
   public org.jbpm.taskmgmt.exe.TaskInstance getTaskInstance()
   {
      if ( !Contexts.isConversationContextActive() ) return null;
      
      Long taskId = Process.instance().getTaskId();
      if (taskId!=null)
      {
         //TODO: do we need to cache this??
         return ManagedJbpmSession.instance().getTaskMgmtSession().loadTaskInstance(taskId);
      }
      else
      {
         return null;
      }
   }
   
   public static org.jbpm.taskmgmt.exe.TaskInstance instance()
   {
      if ( !Contexts.isApplicationContextActive() )
      {
         throw new IllegalStateException("No active application context");
      }
      return (org.jbpm.taskmgmt.exe.TaskInstance) Component.getInstance(TaskInstance.class, ScopeType.APPLICATION, true);
   }
   
}

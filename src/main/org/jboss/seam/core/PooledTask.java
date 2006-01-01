package org.jboss.seam.core;

import javax.faces.context.FacesContext;

import org.jboss.seam.InterceptionType;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jbpm.taskmgmt.exe.TaskInstance;

/**
 * Support for the task list.
 * 
 * @see TaskInstanceList
 * @author Gavin King
 */
@Name( "pooledTask" )
@Scope( ScopeType.APPLICATION )
@Intercept(InterceptionType.NEVER)
public class PooledTask
{
   
   public String assignToCurrentActor()
   {
      Actor actor = Actor.instance();
      if ( actor.getId()==null )
      {
         throw new IllegalStateException("no current actor id defined");
      }
      TaskInstance taskInstance = getTaskInstance();
      if (taskInstance!=null)
      {
         taskInstance.setActorId( actor.getId() );
         ManagedJbpmSession.instance().getSession().flush();
      }
      return "taskAssignedToActor";
   }
   
   private TaskInstance getTaskInstance()
   {
      String taskId = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("taskId");
      return (TaskInstance) ManagedJbpmSession.instance().getTaskMgmtSession().loadTaskInstance( Long.parseLong(taskId) );
   }
   
}

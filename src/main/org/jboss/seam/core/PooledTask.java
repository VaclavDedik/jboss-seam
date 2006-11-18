package org.jboss.seam.core;

import javax.faces.context.FacesContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jbpm.taskmgmt.exe.TaskInstance;

/**
 * Support for the task list.
 * 
 * @see TaskInstanceList
 * @author Gavin King
 */
@Name("org.jboss.seam.core.pooledTask")
@Scope(ScopeType.APPLICATION)
@Install(depends="org.jboss.seam.core.jbpm")
public class PooledTask
{
   
   @Transactional
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
      }
      return "taskAssignedToActor";
   }
   
   private TaskInstance getTaskInstance()
   {
      String taskId = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("taskId");
      return ManagedJbpmContext.instance().getTaskMgmtSession().loadTaskInstance( Long.parseLong(taskId) );
   }
   
}

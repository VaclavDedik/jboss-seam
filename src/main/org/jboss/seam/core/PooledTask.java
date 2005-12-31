package org.jboss.seam.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.faces.context.FacesContext;

import org.jboss.seam.Component;
import org.jboss.seam.InterceptionType;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jbpm.db.JbpmSession;
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
   
   @Factory("pooledTaskInstanceList")
   public void getPooledTaskInstanceList()
   {
      List<TaskInstance> pooledTaskInstanceList = new ArrayList<TaskInstance>();
      Set<String> actorIds = getActor().getGroupActorIds();
      for (String actorId: actorIds )
      {
         pooledTaskInstanceList.addAll( getJbpmSession().getTaskMgmtSession().findPooledTaskInstances(actorId) );
      }
      Contexts.getPageContext().set("pooledTaskInstanceList", pooledTaskInstanceList);
   }

   private JbpmSession getJbpmSession() {
      return (JbpmSession) Component.getInstance(ManagedJbpmSession.class, true);
   }
   
   public String assignToCurrentActor()
   {
      Actor actor = getActor();
      if ( actor.getId()==null )
      {
         throw new IllegalStateException("no current actor id defined");
      }
      TaskInstance taskInstance = getTaskInstance();
      if (taskInstance!=null)
      {
         taskInstance.setActorId( actor.getId() );
         getJbpmSession().getSession().flush();
      }
      return null;
   }
   
   private TaskInstance getTaskInstance()
   {
      String taskId = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("taskId");
      return (TaskInstance) getJbpmSession().getTaskMgmtSession().loadTaskInstance( Long.parseLong(taskId) );
   }

   private Actor getActor() {
      return (Actor) Component.getInstance(Actor.class, true);
   }
   
}

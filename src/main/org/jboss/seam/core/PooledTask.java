package org.jboss.seam.core;

import static org.jboss.seam.annotations.Install.BUILT_IN;

import javax.faces.context.FacesContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jbpm.taskmgmt.exe.TaskInstance;

/**
 * Support for assigning tasks in the pooled task list.
 * 
 * @see TaskInstanceList
 * @author Gavin King
 */
@Name("org.jboss.seam.core.pooledTask")
@Scope(ScopeType.APPLICATION)
@Install(precedence=BUILT_IN, dependencies="org.jboss.seam.core.jbpm")
public class PooledTask
{
   
   /**
    * Assign the TaskInstance with the id passed
    * in the request parameter named "taskId" to
    * the current actor.
    * 
    * @see Actor
    * @return a null outcome only if the task was not found
    */
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
         return "taskAssignedToActor";
      }
      else
      {
         return null;
      }
   }
   
   /**
    * Assign the TaskInstance with the id passed
    * in the request parameter named "taskId" to
    * the given actor id.
    * 
    * @param actorId the jBPM actor id
    * @return a null outcome only if the task was not found
    */
   @Transactional
   public String assign(String actorId)
   {
      TaskInstance taskInstance = getTaskInstance();
      if (taskInstance!=null)
      {
         taskInstance.setActorId(actorId);
         return "taskAssigned";
      }
      else
      {
         return null;
      }
   }
   
   /**
    * @return the TaskInstance with the id passed
    * in the request parameter named "taskId".
    */
   @Transactional
   public TaskInstance getTaskInstance()
   {
      String taskId = (String) FacesContext.getCurrentInstance()
            .getExternalContext()
            .getRequestParameterMap()
            .get("taskId");
      return taskId==null ? 
            null : 
            ManagedJbpmContext.instance()
                  .getTaskMgmtSession()
                  .loadTaskInstance( Long.parseLong(taskId) );
   }
   
}

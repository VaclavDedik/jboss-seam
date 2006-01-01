package org.jboss.seam.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jboss.seam.InterceptionType;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jbpm.taskmgmt.exe.TaskInstance;

/**
 * Support for the pooled task list.
 * 
 * @see TaskInstanceList
 * @author Gavin King
 */
@Name( "pooledTaskInstanceList" )
@Scope( ScopeType.APPLICATION )
@Intercept(InterceptionType.NEVER)
public class PooledTaskInstanceList
{
   
   @Unwrap
   public List<TaskInstance> getPooledTaskInstanceList()
   {
      List<TaskInstance> pooledTaskInstanceList = new ArrayList<TaskInstance>();
      Set<String> actorIds = Actor.instance().getGroupActorIds();
      for (String actorId: actorIds )
      {
         pooledTaskInstanceList.addAll( ManagedJbpmSession.instance().getTaskMgmtSession().findPooledTaskInstances(actorId) );
      }
      return pooledTaskInstanceList;
   }
   
}

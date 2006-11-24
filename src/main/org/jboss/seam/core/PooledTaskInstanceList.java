package org.jboss.seam.core;

import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.Unwrap;
import org.jbpm.taskmgmt.exe.TaskInstance;

/**
 * Support for the pooled task list.
 * 
 * @see TaskInstanceList
 * @author Gavin King
 */
@Name("org.jboss.seam.core.pooledTaskInstanceList")
@Scope(ScopeType.APPLICATION)
@Install(precedence=BUILT_IN, dependencies="org.jboss.seam.core.jbpm")
public class PooledTaskInstanceList
{
   
   @Unwrap
   @Transactional
   public List<TaskInstance> getPooledTaskInstanceList()
   {
      List<TaskInstance> pooledTaskInstanceList = new ArrayList<TaskInstance>();
      Set<String> actorIds = Actor.instance().getGroupActorIds();
      for (String actorId: actorIds )
      {
         pooledTaskInstanceList.addAll( ManagedJbpmContext.instance().getTaskMgmtSession().findPooledTaskInstances(actorId) );
      }
      return pooledTaskInstanceList;
   }
   
}

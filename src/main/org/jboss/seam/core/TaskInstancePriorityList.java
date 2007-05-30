package org.jboss.seam.core;

import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.util.List;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.Unwrap;
import org.jbpm.taskmgmt.exe.TaskInstance;

/**
 * Support for a task list ordered by priority.
 * 
 * @see TaskInstanceList
 * @see PooledTask
 * @author Gavin King
 */
@Name("org.jboss.seam.core.taskInstancePriorityList")
@Scope(APPLICATION)
@Install(precedence=BUILT_IN, dependencies="org.jboss.seam.core.jbpm")
public class TaskInstancePriorityList
{
   
   @Unwrap
   @Transactional
   public List<TaskInstance> getTaskInstanceList()
   {
      return getTaskInstanceList( Actor.instance().getId() );
   }

   private List<TaskInstance> getTaskInstanceList(String actorId)
   {
      if ( actorId == null ) return null;

      return ManagedJbpmContext.instance().getSession()
         .createCriteria(TaskInstance.class)
         .add( Restrictions.eq("actorId", actorId) )
         .addOrder( Order.asc("priority") )
         .list();
   }
   
}

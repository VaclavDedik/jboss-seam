package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.ScopeType.APPLICATION;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jbpm.db.JbpmSession;
import org.jbpm.taskmgmt.exe.TaskInstance;

/**
 * @author <a href="mailto:steve@hibernate.org">Steve Ebersole </a>
 * @author Gavin King
 */
@Name( "taskInstanceList" )
@Scope( APPLICATION )
@Intercept( NEVER )
public class TaskInstanceList
{
   
   @Unwrap
   public List<TaskInstance> getTaskInstanceList()
   {
      return getTaskInstanceList( Actor.instance().getId() );
   }

   private List<TaskInstance> getTaskInstanceList(String actorId)
   {
      if ( actorId == null ) return null;

      JbpmSession jbpmSession = (JbpmSession) Component.getInstance(ManagedJbpmSession.class, true);
      List<TaskInstance> list = new ArrayList<TaskInstance>();
      list.addAll( jbpmSession.getTaskMgmtSession().findTaskInstances(actorId) );
      //TODO: list.addAll( jbpmSession.getTaskMgmtSession().findPooledTaskInstances(actorId) );
      return list;
   }
   
}

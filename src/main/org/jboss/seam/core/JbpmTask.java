/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.core;

import org.jboss.logging.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jbpm.db.JbpmSession;
import org.jbpm.taskmgmt.exe.TaskInstance;

/**
 * Implementation of JbpmTask.
 *
 * @author <a href="mailto:steve@hibernate.org">Steve Ebersole </a>
 * @version $Revision$
 */
@Scope( ScopeType.APPLICATION )
@Name( "org.jbpm.taskmgmt.exe.TaskInstance" )
public class JbpmTask
{
   private static final Logger log = Logger.getLogger( JbpmTask.class );

   @Create()
   public void create(Component component)
   {
      log.trace( "created jbpm task component" );
   }

   @Unwrap
   public TaskInstance getTaskInstance()
   {
      log.trace( "attempting to load jBPM TaskInstance" );
      Long taskId = Manager.instance().getTaskId();
      log.trace( "atskId to load : " + taskId );
      if ( taskId == null )
      {
         throw new IllegalStateException( "could locate task id" );
      }
      JbpmSession session = ( JbpmSession ) Component.getInstance( ManagedJbpmSession.class, true );
      TaskInstance task = session.getTaskMgmtSession().loadTaskInstance( taskId );
      log.trace( "loaded task : " + task );
      return task;
   }

}

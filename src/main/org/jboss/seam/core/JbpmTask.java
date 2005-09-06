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
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.annotations.Destroy;
import org.jbpm.db.JbpmSession;
import org.jbpm.taskmgmt.exe.TaskInstance;

/**
 * Implementation of JbpmTask.
 *
 * @author <a href="mailto:steve@hibernate.org">Steve Ebersole </a>
 * @version $Revision$
 */
@Scope( ScopeType.EVENT )
@Name( "org.jbpm.taskmgmt.exe.TaskInstance" )
public class JbpmTask
{
   public static final String NAME = Seam.getComponentName( JbpmTask.class );
   private static final Logger log = Logger.getLogger( JbpmTask.class );

   private Manager manager;
   private TaskInstance task;

   @Create()
   public void create(Component component)
   {
      // need to lookup the manager on creation to avoid unbroken recurions
      // due to the fact that BusinessProcessContext is a stateful context
      // which is searched prior to app-context; the call to Manager.instance()
      // (if manager not yet created and placed in event context) searches for
      // its component in stateful context; BusinessProcessContext searches
      // contexts for both JbpmProcess and JbpmTask...
      //
      // NOTE : this works on the assumption that BusinessProcessContext
      // is not bootstrapped until after the restore-view phase.
      manager = Manager.instance();
      log.trace( "created jbpm task component" );
   }

   @Unwrap
   public TaskInstance getTaskInstance()
   {
      log.trace( "unwrapping jBPM task" );
      if ( task == null )
      {
         task = getTask();
      }
      return task;
   }

   @Destroy
   public void cleanup()
   {
      task = null;
      manager = null;
   }

   private TaskInstance getTask()
   {
      log.trace( "obtaining task" );

      Long taskId = manager.getTaskId();
      log.trace( "taskId to load : " + taskId );
      if ( taskId == null )
      {
         log.info( "could locate task id" );
         return null;
      }
      JbpmSession session = ( JbpmSession )
              Component.getInstance( ManagedJbpmSession.class, true );
      TaskInstance task = session.getTaskMgmtSession().loadTaskInstance( taskId );
      log.trace( "loaded task : " + task );
      return task;
   }

}

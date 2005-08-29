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
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.contexts.Contexts;
import org.jbpm.db.JbpmSession;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.def.ProcessDefinition;

/**
 * Implementation of JbpmProcess.
 *
 * @author <a href="mailto:steve@hibernate.org">Steve Ebersole </a>
 * @version $Revision$
 */
@Scope( ScopeType.EVENT )
@Name( "org.jbpm.graph.exe.ProcessInstance" )
public class JbpmProcess
{
   public static final String NAME = Seam.getComponentName( JbpmProcess.class );
   public static final String DEFINITION_NAME = NAME + ".definitionName";

   private static final Logger log = Logger.getLogger( JbpmProcess.class );

   private Manager manager;
   private ProcessInstance process;

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
      log.trace( "created jbpm process component" );
   }

   @Unwrap
   public ProcessInstance getProcessInstance()
   {
      log.trace( "unwrapping jBPM process" );
      if ( process == null )
      {
         process = getProcess();
      }
      return process;
   }

   @Destroy
   public void cleanup()
   {
      process = null;
      manager = null;
   }

   private ProcessInstance getProcess()
   {
      log.trace( "obtaining process instance" );
      JbpmSession session = ( JbpmSession )
              Component.getInstance( ManagedJbpmSession.class, true );

      ProcessInstance process = null;
      Long processId = manager.getProcessId();
      if ( processId != null )
      {
         log.trace( "process id for unwrap : " + processId );
         process = session.getGraphSession().loadProcessInstance( processId );
      }
      else
      {
         String definitionName = ( String ) Contexts.getEventContext().get( DEFINITION_NAME );
         log.trace( "process definition name for unwrap : " + definitionName );
         if ( definitionName == null )
         {
            log.info( "could not locate process id nor definition-name" );
            return null;
         }

         ProcessDefinition pd = session.getGraphSession()
                 .findLatestProcessDefinition( definitionName );
         process = new ProcessInstance( pd );
         process.signal();
         session.getGraphSession().saveProcessInstance( process );
         manager.setProcessId( process.getId() );

      }
      return process;
   }

}

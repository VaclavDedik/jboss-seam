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
@Scope( ScopeType.APPLICATION )
@Name( "org.jbpm.graph.exe.ProcessInstance" )
public class JbpmProcess
{
   public static final String NAME = Seam.getComponentName( JbpmProcess.class );
   public static final String DEFINITION_NAME = NAME + ".definitionName";

   private static final Logger log = Logger.getLogger( JbpmProcess.class );

   @Create()
   public void create(Component component)
   {

      log.trace( "created jbpm process component" );
   }

   @Unwrap
   public ProcessInstance getProcessInstance()
   {
      String name = Seam.getComponentName( ManagedJbpmSession.class );
      JbpmSession session = ( JbpmSession ) Component.getInstance( name, true );

      ProcessInstance process = null;
      Long processId = Manager.instance().getProcessId();
      if ( processId != null )
      {
         process = session.getGraphSession().loadProcessInstance( processId );
      }
      else
      {
         String definitionName = ( String ) Contexts.getEventContext().get( DEFINITION_NAME );
         if ( definitionName == null )
         {
            throw new IllegalStateException( "could not locate process id nor definition-name" );
         }

         ProcessDefinition pd = session.getGraphSession()
                 .findLatestProcessDefinition( definitionName );
         process = new ProcessInstance( pd );
         process.signal();
         session.getGraphSession().saveProcessInstance( process );
         Manager.instance().setProcessId( process.getId() );

      }

      return process;
   }

}

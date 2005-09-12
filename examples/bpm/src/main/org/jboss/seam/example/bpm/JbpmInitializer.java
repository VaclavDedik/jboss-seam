// $Id$
package org.jboss.seam.example.bpm;

import java.io.Serializable;
import javax.naming.InitialContext;

import org.jbpm.db.JbpmSessionFactory;
import org.jbpm.db.JbpmSession;
import org.jbpm.graph.def.ProcessDefinition;
import org.hibernate.cfg.Configuration;
import org.jboss.logging.Logger;

/**
 * Implementation of JbpmInitializer.
 *
 * @author Steve Ebersole
 */
public class JbpmInitializer implements Serializable
{
   public static final String JBPM_SF_NAME = "/JbpmSessionFactory";
   private static final Logger log = Logger.getLogger( JbpmInitializer.class );

   private JbpmSessionFactory factory;

   public void initialize()
   {
      factory = buildJbpmSessionFactory();
      initializeJbpmSessionFactory( factory );

      InitialContext ctx = null;
      try
      {
         new InitialContext().bind( JBPM_SF_NAME, factory );
      }
      catch ( Throwable t )
      {
         throw new RuntimeException( t );
      }
      finally
      {
         release( ctx );
      }
   }

   public void release()
   {
      InitialContext ctx = null;
      try
      {
         ctx = new InitialContext();
         factory.getSessionFactory().close();
         ctx.unbind( JBPM_SF_NAME );
      }
      catch ( Throwable t )
      {
         throw new RuntimeException( t );
      }
      finally
      {
         release( ctx );
      }
   }

   private JbpmSessionFactory buildJbpmSessionFactory()
   {
      try
      {
         Configuration cfg = new Configuration();
         cfg.getProperties().clear();
         cfg.configure();
         return JbpmSessionFactory.buildJbpmSessionFactory( cfg );
      }
      catch ( Throwable t )
      {
         log.error( "Error building jbpm session factory", t );
         throw new RuntimeException( "Error building jbpm session factory" );
      }
   }

   private void release(InitialContext ctx)
   {
      if ( ctx != null )
      {
         try
         {
            ctx.close();
         }
         catch ( Throwable ignore )
         {
            // ignore
         }
      }
   }

   private void initializeJbpmSessionFactory(JbpmSessionFactory factory)
   {
      JbpmSession jbpmSession = factory.openJbpmSessionAndBeginTransaction();
      try
      {
         ProcessDefinition processDefinition = ProcessDefinition.parseXmlString( MY_PROCESS_DEF );
         jbpmSession.getGraphSession().saveProcessDefinition( processDefinition );
      }
      finally
      {
         jbpmSession.commitTransactionAndClose();
      }
      System.out.println( "*************************************************" );
      System.out.println( "ProcessDefinition saved!!!!" );
      System.out.println( "*************************************************" );
   }

   private static final String MY_PROCESS_DEF =
           "<process-definition name=\"UserRegistration\">" +
                   "  <start-state>" +
                   "    <transition to=\"pending\">" +
                   "      <action class='org.jboss.seam.example.bpm.JbpmTransitionListener' />" +
                   "    </transition>" +
                   "  </start-state>" +
                   "  <task-node name=\"pending\">" +
                   "    <task name=\"review\"/>" +
                   "    <transition name=\"approve\" to=\"complete\">" +
                   "      <action class='org.jboss.seam.example.bpm.JbpmTransitionListener' />" +
                   "    </transition>" +
                   "    <transition name=\"deny\" to=\"complete\">" +
                   "      <action class='org.jboss.seam.example.bpm.JbpmTransitionListener' />" +
                   "    </transition>" +
                   "  </task-node>" +
                   "  <end-state name=\"complete\"></end-state>" +
                   "</process-definition>";
}

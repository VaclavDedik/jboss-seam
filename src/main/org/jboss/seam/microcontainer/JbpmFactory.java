/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.microcontainer;

import org.jbpm.db.JbpmSessionFactory;
import org.jbpm.db.JbpmSession;
import org.jbpm.graph.def.ProcessDefinition;
import org.hibernate.cfg.Configuration;
import org.jboss.logging.Logger;

import javax.naming.InitialContext;

/**
 * A factory to build/bootstrap a {@link JbpmSessionFactory}.
 *
 * @version $Revision$
 */
public class JbpmFactory
{
   private Logger log = Logger.getLogger( JbpmFactory.class );

   private String jndiName;
   private String hibernateConfigResource;
   private String[] processDefinitions;
   private String[] processDefinitionResources;

   private JbpmSessionFactory factory;

   public String getJndiName()
   {
      return jndiName;
   }

   public void setJndiName(String jndiName)
   {
      this.jndiName = jndiName;
   }

   public String getHibernateConfigResource()
   {
      return hibernateConfigResource;
   }

   public void setHibernateConfigResource(String hibernateConfigResource)
   {
      this.hibernateConfigResource = hibernateConfigResource;
   }

   public String[] getProcessDefinitions()
   {
      return processDefinitions;
   }

   public void setProcessDefinitions(String[] processDefinitions)
   {
      this.processDefinitions = processDefinitions;
   }

   public String[] getProcessDefinitionResources()
   {
      return processDefinitionResources;
   }

   public void setProcessDefinitionResources(String[] processDefinitionResources)
   {
      this.processDefinitionResources = processDefinitionResources;
   }

   public JbpmSessionFactory getJbpmSessionFactory() throws Exception
   {
//      JbpmSessionFactory factory = buildJbpmSessionFactory();
//      installProcessDefinitions( factory );
//      bind( factory );

      return factory;
   }

   public void initialize() throws Exception
   {
      log.trace( "Starting initialization" );
      factory = buildJbpmSessionFactory();
      installProcessDefinitions( factory );
      bind( factory );
   }

   public void cleanup()
   {
      try
      {
         unbind();
      }
      catch( Throwable t )
      {
         log.debug( "Problem unbinding jBPM session factory from JNDI", t );
      }

      try
      {
         factory.getSessionFactory().close();
      }
      catch( Throwable t )
      {
         log.debug( "Problem cleaning up jBPM session factory", t );
      }
   }

   private JbpmSessionFactory buildJbpmSessionFactory() throws Exception
   {
      Configuration cfg = new Configuration();
      cfg.getProperties().clear();
      if ( hibernateConfigResource == null )
      {
         log.trace( "Configuring Hibernate from default cfg" );
         cfg.configure();
      }
      else
      {
         log.trace( "Configuring Hibernate from resource : " + hibernateConfigResource );
         cfg.configure( hibernateConfigResource );
      }
      return JbpmSessionFactory.buildJbpmSessionFactory( cfg );
   }

   private void installProcessDefinitions(JbpmSessionFactory factory)
   {
      JbpmSession jbpmSession = factory.openJbpmSessionAndBeginTransaction();
      try
      {
         if ( processDefinitions != null )
         {
            for ( final String definition : processDefinitions )
            {
               ProcessDefinition processDefinition = ProcessDefinition.parseXmlString( definition );
               log.trace( "installing process definition : " + processDefinition.getName() );
               jbpmSession.getGraphSession().saveProcessDefinition( processDefinition );
            }
         }

         if ( processDefinitionResources != null )
         {
            for ( final String definitionResource : processDefinitionResources )
            {
               ProcessDefinition processDefinition = ProcessDefinition.parseXmlResource( definitionResource );
               log.trace( "installing process definition : " + processDefinition.getName() );
               jbpmSession.getGraphSession().saveProcessDefinition( processDefinition );
            }
         }
      }
      finally
      {
         jbpmSession.commitTransactionAndClose();
      }
   }

   private void bind(JbpmSessionFactory factory) throws Exception
   {
      InitialContext ctx = null;
      try
      {
         ctx = new InitialContext();
         ctx.bind( jndiName, factory );
      }
      finally
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
   }

   private void unbind() throws Exception
   {
      InitialContext ctx = null;
      try
      {
         ctx = new InitialContext();
         ctx.unbind( jndiName );
      }
      finally
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
   }
}

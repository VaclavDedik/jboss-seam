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

import javax.naming.InitialContext;

/**
 * A factory to build/bootstrap a {@link JbpmSessionFactory}.
 *
 * @version $Revision$
 */
public class JbpmFactory
{
   private String jndiName;
   private String hibernateConfigResource;
   private String[] processDefinitions;
   private String[] processDefinitionResources;

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
      JbpmSessionFactory factory = buildJbpmSessionFactory();
      installProcessDefinitions( factory );
      bind( factory );

      return factory;
   }

   private JbpmSessionFactory buildJbpmSessionFactory() throws Exception
   {
      Configuration cfg = new Configuration();
      cfg.getProperties().clear();
      if ( hibernateConfigResource == null )
      {
         cfg.configure();
      }
      else
      {
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
               jbpmSession.getGraphSession().saveProcessDefinition( processDefinition );
            }
         }

         if ( processDefinitionResources != null )
         {
            for ( final String definitionResource : processDefinitionResources )
            {
               ProcessDefinition processDefinition = ProcessDefinition.parseXmlResource( definitionResource );
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
}

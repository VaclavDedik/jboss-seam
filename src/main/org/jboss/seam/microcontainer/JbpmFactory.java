/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.microcontainer;

import org.jboss.logging.Logger;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.graph.def.ProcessDefinition;

/**
 * A factory to build/bootstrap a {@link JbpmConfiguration}.
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

   private JbpmConfiguration jbpmConfiguration;

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

   public JbpmConfiguration getJbpmConfiguration()
   {
      return jbpmConfiguration;
   }

   public void initialize() throws Exception
   {
      log.trace( "Starting initialization" );
      jbpmConfiguration = JbpmConfiguration.getInstance();
      installProcessDefinitions( jbpmConfiguration );
   }

   private void installProcessDefinitions(JbpmConfiguration configuration)
   {
      JbpmContext jbpmContext = configuration.createJbpmContext();
      try
      {
         if ( processDefinitions != null )
         {
            for ( final String definition : processDefinitions )
            {
               ProcessDefinition processDefinition = ProcessDefinition.parseXmlString( definition );
               log.trace( "installing process definition : " + processDefinition.getName() );
               jbpmContext.deployProcessDefinition(processDefinition);
            }
         }

         if ( processDefinitionResources != null )
         {
            for ( final String definitionResource : processDefinitionResources )
            {
               ProcessDefinition processDefinition = ProcessDefinition.parseXmlResource( definitionResource );
               log.trace( "installing process definition : " + processDefinition.getName() );
               jbpmContext.deployProcessDefinition(processDefinition);
            }
         }
      }
      catch (RuntimeException e)
      {
         throw new RuntimeException("could not deploy a process definition", e);
      }
      finally
      {
         jbpmContext.close();
      }
   }
   
}

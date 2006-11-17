package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cfg.Environment;
import org.hibernate.lob.ReaderInputStream;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.jbpm.SeamVariableResolver;
import org.jboss.seam.pageflow.PageflowHelper;
import org.jboss.seam.util.Naming;
import org.jboss.seam.util.Resources;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.jpdl.el.impl.JbpmExpressionEvaluator;
import org.jbpm.persistence.db.DbPersistenceServiceFactory;
import org.xml.sax.InputSource;

/**
 * A seam component that boostraps a JBPM SessionFactory
 * 
 * @author Gavin King
 * @author <a href="mailto:steve@hibernate.org">Steve Ebersole</a>
 * @author Norman Richards
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 */
@Scope(ScopeType.APPLICATION)
@Intercept(NEVER)
@Startup(depends={"org.jboss.seam.core.microcontainer", "org.jboss.seam.core.ejb"})
@Name("org.jboss.seam.core.jbpm")
public class Jbpm 
{
   private static final Log log = LogFactory.getLog(Jbpm.class);
   
   private JbpmConfiguration jbpmConfiguration;
   private String jbpmConfigurationJndiName;
   private String[] processDefinitions;
   private String[] pageflowDefinitions;
   private Map<String, ProcessDefinition> pageflowProcessDefinitions = new HashMap<String, ProcessDefinition>();

   @Create
   public void startup() throws Exception
   {
      log.trace( "Starting jBPM" );
      installProcessDefinitions();
      installPageflowDefinitions();
      JbpmExpressionEvaluator.setVariableResolver( new SeamVariableResolver() );
   }

   @Destroy
   public void shutdown()
   {
      if (jbpmConfiguration!=null) 
      {
         jbpmConfiguration.close();
      }
   }
   
   public JbpmConfiguration getJbpmConfiguration()
   {
      if (jbpmConfiguration==null)
      {
         initJbpmConfiguration();
      }
      return jbpmConfiguration;
   }

   private void initJbpmConfiguration()
   {
      if (jbpmConfigurationJndiName==null)
      {
         jbpmConfiguration = JbpmConfiguration.getInstance();
      }
      else
      {
         try
         {
            jbpmConfiguration = (JbpmConfiguration) Naming.getInitialContext().lookup(jbpmConfigurationJndiName);
         }
         catch (NamingException ne)
         {
            throw new IllegalArgumentException("JbpmConfiguration not found in JNDI", ne);
         }
      }

      DbPersistenceServiceFactory dbpsf = (DbPersistenceServiceFactory) jbpmConfiguration.getServiceFactory("persistence");
      if (Naming.getInitialContextProperties()!=null)
      {
         // Prefix regular JNDI properties for Hibernate
         Hashtable<String, String> hash = Naming.getInitialContextProperties();
         Properties prefixed = new Properties();
         for (Map.Entry<String, String> entry: hash.entrySet() )
         {
            prefixed.setProperty( Environment.JNDI_PREFIX + "." + entry.getKey(), entry.getValue() );
         }
  
         dbpsf.getConfiguration().getProperties().putAll(prefixed);
      }
   }

   public ProcessDefinition getPageflowProcessDefinition(String pageflowName)
   {
      return pageflowProcessDefinitions.get(pageflowName);
   }
   
   public ProcessDefinition getPageflowDefinitionFromResource(String resourceName)
   {
      InputStream resource = Resources.getResourceAsStream(resourceName);
      if (resource==null)
      {
         throw new IllegalArgumentException("pageflow resource not found: " + resourceName);
      }
      return PageflowHelper.parseInputSource( new InputSource(resource) );
   }
   
   public ProcessDefinition getProcessDefinitionFromResource(String resourceName) 
   {
      InputStream resource = Resources.getResourceAsStream(resourceName);
      if (resource==null)
      {
         throw new IllegalArgumentException("process definition resource not found: " + resourceName);
      }
      return ProcessDefinition.parseXmlInputStream(resource);
   }

   public String[] getPageflowDefinitions() {
      return pageflowDefinitions;
   }

   public void setPageflowDefinitions(String[] pageflowDefinitions) {
      this.pageflowDefinitions = pageflowDefinitions;
   }
   
   public String[] getProcessDefinitions() {
      return processDefinitions;
   }

   public void setProcessDefinitions(String[] processDefinitions) {
      this.processDefinitions = processDefinitions;
   }
   
   /**
    * Dynamically deploy a page flow definition, if a pageflow with an 
    * identical name already exists, the pageflow is updated.
    * 
    * @return true if the pageflow definition has been updated
    */
   public boolean deployPageflowDefinition(ProcessDefinition pageflowDefinition) 
   {
      return pageflowProcessDefinitions.put( pageflowDefinition.getName(), pageflowDefinition )!=null;
   }
   
   /**
    * Read a pageflow definition
    * 
    * @param pageflowDefinition the pageflow as an XML string
    */
   public ProcessDefinition getPageflowDefinitionFromXml(String pageflowDefinition)
   {
      return PageflowHelper.parseInputSource( new InputSource( new ReaderInputStream( new StringReader(pageflowDefinition) ) ) );
   }
   
   /**
    * Read a process definition
    * 
    * @param processDefinition the process as an XML string
    */
   public ProcessDefinition getProcessDefinitionFromXml(String processDefinition)
   {
      return ProcessDefinition.parseXmlInputStream( new ReaderInputStream( new StringReader(processDefinition) ) );
   }
   
   /**
    * Remove a pageflow definition
    * 
    * @param pageflowName Name of the pageflow to remove
    * @return true if the pageflow definition has been removed
    */
   public boolean undeployPageflowDefinition(String pageflowName) 
   {     
      return pageflowProcessDefinitions.remove(pageflowName)!=null;
   }
   
   private void installPageflowDefinitions() {
      if ( pageflowDefinitions!=null )
      {
         for (String pageflow: pageflowDefinitions)
         {
            ProcessDefinition pd = getPageflowDefinitionFromResource(pageflow);
            pageflowProcessDefinitions.put( pd.getName(), pd );
         }
      }
   }

   private void installProcessDefinitions()
   {
      if ( processDefinitions!=null && processDefinitions.length>0 )
      {
         JbpmContext jbpmContext = getJbpmConfiguration().createJbpmContext();
         try
         {
            for ( String definitionResource : processDefinitions )
            {
               ProcessDefinition processDefinition = ProcessDefinition.parseXmlResource( definitionResource );
               if (log.isDebugEnabled())
               {
                  log.debug( "deploying process definition : " + processDefinition.getName() );
               }
               jbpmContext.deployProcessDefinition(processDefinition);
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
   
   public static Jbpm instance()
   {
      if ( !Contexts.isApplicationContextActive() )
      {
         throw new IllegalStateException("No application context active");
      }
      if ( !Init.instance().isJbpmInstalled() )
      {
         throw new IllegalStateException("jBPM support is not installed (use components.xml to install it)");
      }
      return (Jbpm) Contexts.getApplicationContext().get(Jbpm.class);
   }

   protected String getJbpmConfigurationJndiName()
   {
      return jbpmConfigurationJndiName;
   }

   protected void setJbpmConfigurationJndiName(String jbpmConfigurationJndiName)
   {
      this.jbpmConfigurationJndiName = jbpmConfigurationJndiName;
   }
   
}

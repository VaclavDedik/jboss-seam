package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cfg.Environment;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.jbpm.PageflowParser;
import org.jboss.seam.jbpm.SeamVariableResolver;
import org.jboss.seam.util.Naming;
import org.jboss.seam.util.Resources;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.jpdl.el.impl.JbpmExpressionEvaluator;
import org.jbpm.pageflow.PageflowHelper;
import org.jbpm.persistence.db.DbPersistenceServiceFactory;
import org.xml.sax.InputSource;

/**
 * A seam component that boostraps a JBPM SessionFactory
 * 
 * @author Gavin King
 * @author <a href="mailto:steve@hibernate.org">Steve Ebersole </a>
 * @author Norman Richards
 */
@Scope(ScopeType.APPLICATION)
@Intercept(NEVER)
@Startup(depends={"org.jboss.seam.core.microcontainer", "org.jboss.seam.core.ejb"})
@Name("org.jboss.seam.core.jbpm")
public class Jbpm 
{
   private static final Log log = LogFactory.getLog( Jbpm.class );
   
   public static final String PROCESS_DEFINITIONS = Seam.getComponentName(Jbpm.class) + ".processDefinitions";
   public static final String PAGEFLOW_DEFINITIONS = Seam.getComponentName(Jbpm.class) + ".pageflowDefinitions";

   private JbpmConfiguration jbpmConfiguration;
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
      jbpmConfiguration.close();
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
      jbpmConfiguration = JbpmConfiguration.getInstance();
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
   
   private ProcessDefinition getPageflowDefinitionFromResource(String resourceName)
   {
      InputStream resource = Resources.getResourceAsStream(resourceName);
      if (resource==null)
      {
         throw new IllegalArgumentException("pageflow resource not found: " + resourceName);
      }
      return PageflowHelper.parseInputSource(new InputSource(resource));
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
               log.trace( "deploying process definition : " + processDefinition.getName() );
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
      return (Jbpm) Contexts.getApplicationContext().get(Jbpm.class);
   }
   
}

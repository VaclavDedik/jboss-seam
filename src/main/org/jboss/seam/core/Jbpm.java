package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.microcontainer.JbpmFactory;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.graph.def.ProcessDefinition;

/**
 * A seam component that boostraps a JBPM SessionFactory
 * 
 * @author Gavin King
 * @author <a href="mailto:steve@hibernate.org">Steve Ebersole </a>
 * @author Norman Richards
 */
@Scope(ScopeType.APPLICATION)
@Intercept(NEVER)
@Startup(depends="org.jboss.seam.core.microcontainer")
@Name("org.jboss.seam.core.jbpm")
public class Jbpm 
{
   public static final String PROCESS_DEFINITIONS = Seam.getComponentName(Jbpm.class) + ".processDefinitions";

   private JbpmFactory jbpmFactory;
   private String[] processDefinitions;
   private String[] pageflowDefinitions;
   private Map<String, ProcessDefinition> pageflowProcessDefinitions;
   
   @Create
   public void startup() throws Exception
   {
      jbpmFactory = new JbpmFactory();
      jbpmFactory.setJndiName( Init.instance().getJbpmSessionFactoryName() );
      jbpmFactory.setProcessDefinitionResources(processDefinitions);
      jbpmFactory.initialize();
      
      pageflowProcessDefinitions = new HashMap<String, ProcessDefinition>(pageflowDefinitions.length);
      for (String pageflow: pageflowDefinitions)
      {
         ProcessDefinition pd = getProcessDefinitionFromResource(pageflow);
         pageflowProcessDefinitions.put( pd.getName(), pd );
      }
   }
   
   @Destroy
   public void shutdown()
   {
      jbpmFactory = null;
   }

   public String[] getProcessDefinitions() {
      return processDefinitions;
   }

   public void setProcessDefinitions(String[] processDefinitions) {
      this.processDefinitions = processDefinitions;
   }
   
   /**
    * Dynamically load a new jBPM process definition from a resource at runtime.
    * 
    * @param resourceName the name of a resource containing the process definition
    * @param makeLatestVersion the loaded definition should be considered the latest version
    */
   public void loadProcessDefinition(String resourceName, boolean makeLatestVersion) {
      JbpmContext context = jbpmFactory.getJbpmConfiguration().createJbpmContext();
      try
      {
         ProcessDefinition processDefinition = getProcessDefinitionFromResource(resourceName);
         context.deployProcessDefinition(processDefinition);
         context.getSession().flush(); //TODO: why???
      }
      catch (RuntimeException e)
      {
         throw new RuntimeException("could not deploy process definition: " + resourceName, e);
      }
      finally
      {
         context.close();
      }
   }

   private ProcessDefinition getProcessDefinitionFromResource(String resourceName) {
      InputStream resource = Thread.currentThread().getContextClassLoader()
            .getResourceAsStream(resourceName);
      if (resource==null)
      {
         throw new IllegalArgumentException("resource not found: " + resourceName);
      }
      
      return ProcessDefinition.parseXmlInputStream(resource);
   }

   public String[] getPageflowDefinitions() {
      return pageflowDefinitions;
   }

   public void setPageflowDefinitions(String[] pageflowDefinitions) {
      this.pageflowDefinitions = pageflowDefinitions;
   }
   
   public ProcessDefinition getPageflowDefinition(String pageflowName)
   {
      return pageflowProcessDefinitions.get(pageflowName);
   }
   
   public static Jbpm instance()
   {
      return (Jbpm) Contexts.getApplicationContext().get(Jbpm.class);
   }
   
   public JbpmConfiguration getJbpmConfiguration()
   {
      return jbpmFactory.getJbpmConfiguration();
   }
  
}

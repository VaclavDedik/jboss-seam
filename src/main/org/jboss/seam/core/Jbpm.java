package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.jboss.logging.Logger;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.contexts.Contexts;
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
   private Logger log = Logger.getLogger( Jbpm.class );
   
   public static final String PROCESS_DEFINITIONS = Seam.getComponentName(Jbpm.class) + ".processDefinitions";
   
   private JbpmConfiguration jbpmConfiguration;
   private String[] processDefinitions;
   private String[] pageflowDefinitions;
   private Map<String, ProcessDefinition> pageflowProcessDefinitions;
   
   @Create
   public void startup() throws Exception
   {
      log.trace( "Starting initialization" );
      jbpmConfiguration = JbpmConfiguration.getInstance();
      installProcessDefinitions();
      installPageflowDefinitions();
   }

   @Destroy
   public void shutdown()
   {
      //TODO: don't I need to destroy it somehow???
      jbpmConfiguration = null;
   }
   
   public JbpmConfiguration getJbpmConfiguration()
   {
      return jbpmConfiguration;
   }

   public ProcessDefinition getPageflowProcessDefinition(String pageflowName)
   {
      return pageflowProcessDefinitions.get(pageflowName);
   }
   
   public ProcessDefinition getProcessDefinitionFromResource(String resourceName) {
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
   
   public String[] getProcessDefinitions() {
      return processDefinitions;
   }

   public void setProcessDefinitions(String[] processDefinitions) {
      this.processDefinitions = processDefinitions;
   }
   
   private void installPageflowDefinitions() {
      pageflowProcessDefinitions = new HashMap<String, ProcessDefinition>(pageflowDefinitions.length);
      for (String pageflow: pageflowDefinitions)
      {
         ProcessDefinition pd = getProcessDefinitionFromResource(pageflow);
         pageflowProcessDefinitions.put( pd.getName(), pd );
      }
   }
   
   private void installProcessDefinitions()
   {
      if ( processDefinitions != null )
      {
         JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
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

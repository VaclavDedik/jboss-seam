package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.microcontainer.JbpmFactory;

/**
 * A seam component that boostraps a JBPM SessionFactory
 * 
 * @author Gavin King
 * @author <a href="mailto:steve@hibernate.org">Steve Ebersole </a>
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
   
   @Create
   public void startup() throws Exception
   {
      jbpmFactory = new JbpmFactory();
      jbpmFactory.setJndiName( Init.instance().getJbpmSessionFactoryName() );
      jbpmFactory.setProcessDefinitionResources(processDefinitions);
      jbpmFactory.initialize();
   }
   
   @Destroy
   public void shutdown()
   {
      jbpmFactory.cleanup();
      jbpmFactory = null;
   }

   public String[] getProcessDefinitions() {
      return processDefinitions;
   }

   public void setProcessDefinitions(String[] processDefinitions) {
      this.processDefinitions = processDefinitions;
   }
}

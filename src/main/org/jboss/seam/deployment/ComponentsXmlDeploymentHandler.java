package org.jboss.seam.deployment;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * The {@link ComponentsXmlDeploymentHandler} components.xml and .component.xml files 
 * 
 * @author Pete Muir
 *
 */
public class ComponentsXmlDeploymentHandler extends AbstractDeploymentHandler
{
   /**
    * Name under which this {@link DeploymentHandler} is registered
    */
   public static final String NAME = "org.jboss.seam.deployment.ComponentsXmlDeploymentHandler";

   private Set<String> resources;
   
   public ComponentsXmlDeploymentHandler()
   {
      resources = new HashSet<String>();
   }
   
   /**
    * Get paths to components.xml files
    */
   public Set<String> getResources() 
   {
       return Collections.unmodifiableSet(resources);
   }

   /**
    * @see DeploymentHandler#handle(String, ClassLoader)
    */
   public void handle(String name, ClassLoader classLoader)
   {
      if (name.endsWith(".component.xml") || name.endsWith("/components.xml")) 
      {
          // we want to skip over known meta-directories since Seam will auto-load these without a scan
          if (!name.startsWith("WEB-INF/") && !name.startsWith("META-INF/")) 
          {
              resources.add(name);
          }           
      }
           
   }
   
   public String getName()
   {
      return NAME;
   }
   
}

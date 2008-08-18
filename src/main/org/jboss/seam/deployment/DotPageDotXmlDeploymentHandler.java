package org.jboss.seam.deployment;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jboss.seam.contexts.Contexts;

/**
 * The {@link DotPageDotXmlDeploymentHandler} process .page.xml files
 *  
 * @author Pete Muir
 *
 */
public class DotPageDotXmlDeploymentHandler extends AbstractDeploymentHandler
{
   /**
    * Name under which this {@link DeploymentHandler} is registered
    */
   public static final String NAME = "org.jboss.seam.deployment.DotPageDotXmlDeploymentHandler";

   private Set<String> files;
   
   public DotPageDotXmlDeploymentHandler()
   {
      files = new HashSet<String>();
   }

   /**
    * Get annotated Seam components
    */
   public Set<String> getFiles()
   {
      return Collections.unmodifiableSet(files);
   }

   /**
    * @see DeploymentHandler#handle(String, ClassLoader)
    */
   public void handle(String name, ClassLoader classLoader)
   {
      if (name.endsWith(".page.xml")) 
      {
         files.add(name);
      }
   }
   
   public String getName()
   {
      return NAME;
   }
   
   public static DotPageDotXmlDeploymentHandler instance()
   {
      if (Contexts.isEventContextActive())
      {
         if (Contexts.getEventContext().isSet(HotDeploymentStrategy.NAME))
         {
            DeploymentStrategy deploymentStrategy = (DeploymentStrategy) Contexts.getEventContext().get(StandardDeploymentStrategy.NAME); 
            Object deploymentHandler = deploymentStrategy.getDeploymentHandlers().get(NAME);
            if (deploymentHandler != null)
            {
               return (DotPageDotXmlDeploymentHandler) deploymentHandler;
            }
         }
         return null;
      }
      else
      {
         throw new IllegalStateException("Event context not active");
      }
   }
   
   public static DotPageDotXmlDeploymentHandler hotInstance()
   {
      if (Contexts.isEventContextActive())
      {
         DeploymentStrategy deploymentStrategy = (DeploymentStrategy) Contexts.getEventContext().get(HotDeploymentStrategy.NAME);
         if (deploymentStrategy != null)
         {
            Object deploymentHandler = deploymentStrategy.getDeploymentHandlers().get(NAME);
            if (deploymentHandler != null)
            {
               return (DotPageDotXmlDeploymentHandler) deploymentHandler;
            }
         }
         return null;
      }
      else
      {
         throw new IllegalStateException("Event context not active");
      }
   }
   
}

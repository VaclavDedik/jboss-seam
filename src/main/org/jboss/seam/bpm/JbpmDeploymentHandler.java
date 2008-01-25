package org.jboss.seam.bpm;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.deployment.AbstractDeploymentHandler;

/**
 * Handles jBPM resources discovered on scan
 * 
 * TODO Hook this into the Jbpm component
 * 
 * @author Pete Muir
 *
 */
public class JbpmDeploymentHandler extends AbstractDeploymentHandler
{
   
   private List<String> jpdlResources;
   
   public JbpmDeploymentHandler()
   {
      jpdlResources = new ArrayList<String>();
   }
   
   public static final String NAME = "org.jboss.seam.bpm.JbpmDeploymentHandler";

   public String getName()
   {
      return NAME;
   }

   public void handle(String name, ClassLoader classLoader)
   {
      if ( name.endsWith(".jpdl.xml") && !name.startsWith(".gpd") )
      {
         jpdlResources.add(name);
      }
   }
   
   public List<String> getJpdlResources()
   {
      return jpdlResources;
   }

}

//$Id$
package org.jboss.seam.servlet;

import java.net.URL;
import java.util.Map;

import javax.management.MBeanServer;

import org.jboss.mx.util.MBeanServerLocator;
import org.jboss.seam.components.Components;
import org.jboss.seam.components.Settings;
import org.jboss.seam.deployment.SeamDeployer;
import org.jboss.seam.deployment.SeamModule;

public class SeamJBossServletContextListener extends SeamServletContextListener
{

   @Override
   protected void addComponents(Settings settings, Components components)
   {
      super.addComponents( settings, components );
      MBeanServer mBeanServer = MBeanServerLocator.locate();
      try
      {
         Map<URL, SeamModule> seamModules = (Map<URL, SeamModule>) mBeanServer.getAttribute(SeamDeployer.OBJECT_NAME, "SeamModules");
         for (SeamModule module: seamModules.values())
         {
            for (Class componentClass: module.getComponentClasses())
            {
               components.addComponent(componentClass);
            }
         }
      }
      catch (Exception e)
      {
         throw new RuntimeException("could not connect to Seam MBean server");
      }   
   }

}

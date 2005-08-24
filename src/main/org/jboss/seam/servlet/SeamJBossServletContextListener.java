//$Id$
package org.jboss.seam.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.jboss.seam.init.JBossInitialization;


public class SeamJBossServletContextListener implements ServletContextListener
{

   public void contextDestroyed(ServletContextEvent event)
   {
      new JBossInitialization().init( event.getServletContext() );
   }

   public void contextInitialized(ServletContextEvent event) {}


}

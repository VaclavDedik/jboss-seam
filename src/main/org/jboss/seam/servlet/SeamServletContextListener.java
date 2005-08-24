//$Id$
package org.jboss.seam.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.jboss.seam.init.Initialization;

public class SeamServletContextListener implements ServletContextListener
{

   public void contextInitialized(ServletContextEvent event)
   {
      new Initialization().init( event.getServletContext() );
   }

   public void contextDestroyed(ServletContextEvent event) { }

}

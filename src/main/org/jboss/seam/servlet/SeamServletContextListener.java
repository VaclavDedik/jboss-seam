//$Id$
package org.jboss.seam.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.jboss.logging.Logger;
import org.jboss.seam.init.Initialization;

public class SeamServletContextListener implements ServletContextListener
{
   
   private static final Logger log = Logger.getLogger(SeamServletContextListener.class);

   public void contextInitialized(ServletContextEvent event)
   {
      log.info("Welcome to Seam");
      new Initialization().init( event.getServletContext() );
   }

   public void contextDestroyed(ServletContextEvent event) { }

}

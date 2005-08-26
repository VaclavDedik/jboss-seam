//$Id$
package org.jboss.seam.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.jboss.logging.Logger;
import org.jboss.seam.init.Initialization;

/**
 * Bootstraps Seam in a web container
 * 
 * @author Gavin King
 */
public class SeamStartupListener implements ServletContextListener
{
   
   private static final Logger log = Logger.getLogger(SeamStartupListener.class);

   public void contextInitialized(ServletContextEvent event)
   {
      log.info("Welcome to Seam");
      new Initialization( event.getServletContext() ).init();
   }

   public void contextDestroyed(ServletContextEvent event) { }

}

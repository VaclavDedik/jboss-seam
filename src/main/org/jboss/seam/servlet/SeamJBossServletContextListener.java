//$Id$
package org.jboss.seam.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.jboss.logging.Logger;
import org.jboss.seam.init.JBossInitialization;


public class SeamJBossServletContextListener implements ServletContextListener
{

   private static final Logger log = Logger.getLogger(SeamJBossServletContextListener.class);
   
   public void contextInitialized(ServletContextEvent event) {
      log.info("Welcome to Seam on JBoss");
      new JBossInitialization().init( event.getServletContext() );
   }

   public void contextDestroyed(ServletContextEvent event)
   {
   }


}

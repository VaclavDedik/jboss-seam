//$Id$
package org.jboss.seam.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.jboss.logging.Logger;
import org.jboss.seam.init.JBossInitialization;

/**
 * Boostraps Seam inside JBoss
 * 
 * @author Gavin King
 */
public class SeamJBossStartupListener implements ServletContextListener
{

   private static final Logger log = Logger.getLogger(SeamJBossStartupListener.class);
   
   public void contextInitialized(ServletContextEvent event) {
      log.info("Welcome to Seam on JBoss");
      new JBossInitialization().init( event.getServletContext() );
   }

   public void contextDestroyed(ServletContextEvent event) {}

}

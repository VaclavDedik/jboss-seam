//$Id$
package org.jboss.seam.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.jboss.logging.Logger;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.init.Initialization;

/**
 * Destroys Seam components when the app is undeployed
 * 
 * @author Gavin King
 */
public class SeamListener implements ServletContextListener, HttpSessionListener
{

   private static final Logger log = Logger.getLogger(ServletContextListener.class);

   public void contextInitialized(ServletContextEvent event) {
      log.info("Welcome to Seam");
      new Initialization( event.getServletContext() ).init();
   }

   public void contextDestroyed(ServletContextEvent event) {
      Lifecycle.endApplication( event.getServletContext() );
   }

   public void sessionCreated(HttpSessionEvent event) {}

   public void sessionDestroyed(HttpSessionEvent event) {
      Lifecycle.endSession( event.getSession() );
   }

}

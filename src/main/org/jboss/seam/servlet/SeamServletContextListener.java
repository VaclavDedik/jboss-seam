//$Id$
package org.jboss.seam.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.jboss.seam.contexts.Contexts;

/**
 * Destroys Seam components when the app is undeployed
 * 
 * @author Gavin King
 */
public class SeamServletContextListener implements ServletContextListener
{

   public void contextInitialized(ServletContextEvent event) {
      Contexts.beginApplication( event.getServletContext() );
   }

   public void contextDestroyed(ServletContextEvent event) {
      Contexts.endApplication();
   }

}

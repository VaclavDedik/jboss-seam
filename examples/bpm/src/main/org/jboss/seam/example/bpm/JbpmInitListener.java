// $Id$
package org.jboss.seam.example.bpm;

import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;

/**
 * Implementation of JbpmInitListener.
 *
 * @author Steve Ebersole
 */
public class JbpmInitListener implements ServletContextListener
{
   private JbpmInitializer initializer;

   public void contextInitialized(ServletContextEvent servletContextEvent)
   {
      initializer = new JbpmInitializer();
      initializer.initialize();
   }

   public void contextDestroyed(ServletContextEvent servletContextEvent)
   {
      initializer.release();
      initializer = null;
   }
}

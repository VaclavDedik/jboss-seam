//$Id$
package org.jboss.seam.contexts;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.jboss.logging.Logger;

public class SeamApplicationListener implements ServletContextListener
{
   
   private static final Logger log = Logger.getLogger(SeamApplicationListener.class);

   public void contextInitialized(ServletContextEvent event) {}

   public void contextDestroyed(ServletContextEvent event) {
       log.info("destroying application context");
      Contexts.destroy( new WebApplicationContext( event.getServletContext() ) );
   }

}

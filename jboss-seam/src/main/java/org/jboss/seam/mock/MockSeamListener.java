/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.mock;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.jboss.seam.Seam;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

/**
 * Used to retrieve real ServletContext for the AbstractSeamTest startSeam
 * 
 * @author Marek Schmidt
 */
public class MockSeamListener implements ServletContextListener
{
   private static final LogProvider log = Logging.getLogProvider(ServletContextListener.class);
   
   private static ServletContext servletContext;
   
   public void contextInitialized(ServletContextEvent event) 
   {
      log.info( "Welcome to Mock Seam " + Seam.getVersion() );
      event.getServletContext().setAttribute( Seam.VERSION, Seam.getVersion() );
      servletContext = event.getServletContext();
   }
   
   public void contextDestroyed(ServletContextEvent event) 
   {
   }
   
   public static ServletContext getServletContext() {
      return servletContext;
   }
}

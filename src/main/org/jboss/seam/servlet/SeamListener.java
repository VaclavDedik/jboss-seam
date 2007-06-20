/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.jboss.seam.Seam;
import org.jboss.seam.contexts.ServletLifecycle;
import org.jboss.seam.init.Initialization;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

/**
 * Drives certain Seam functionality such as initialization and cleanup
 * of application and session contexts from the web application lifecycle.
 * 
 * @author Gavin King
 */
public class SeamListener implements ServletContextListener, HttpSessionListener
{
   private static final LogProvider log = Logging.getLogProvider(ServletContextListener.class);
   
   public void contextInitialized(ServletContextEvent event) 
   {
      log.info( "Welcome to Seam " + Seam.getVersion() );
      ServletLifecycle.beginApplication( event.getServletContext() );
      new Initialization( event.getServletContext() ).create().init();
   }
   
   public void contextDestroyed(ServletContextEvent event) 
   {
      ServletLifecycle.endApplication();
   }
   
   public void sessionCreated(HttpSessionEvent event) 
   {
      ServletLifecycle.beginSession( event.getSession() );
   }
   
   public void sessionDestroyed(HttpSessionEvent event) 
   {
      ServletLifecycle.endSession( event.getSession() );
   }
   
}

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.init.Initialization;

/**
 * Drives certain Seam functionality such as initialization and cleanup
 * of application and session contexts from the web application lifecycle.
 * 
 * @author Gavin King
 */
public class SeamListener implements ServletContextListener, HttpSessionListener
{

   private static final Log log = LogFactory.getLog(ServletContextListener.class);

   public void contextInitialized(ServletContextEvent event) {
      log.info("Welcome to Seam 1.1.0.GA");
      Lifecycle.setServletContext( event.getServletContext() );
      new Initialization( event.getServletContext() ).init();
   }

   public void contextDestroyed(ServletContextEvent event) {
      Lifecycle.endApplication( event.getServletContext() );
   }

   public void sessionCreated(HttpSessionEvent event) {
      Lifecycle.beginSession( event.getSession().getServletContext(), new ServletSessionImpl( event.getSession() ) );
   }

   public void sessionDestroyed(HttpSessionEvent event) {
      Lifecycle.endSession( event.getSession().getServletContext(), new ServletSessionImpl( event.getSession() ) );
   }

}

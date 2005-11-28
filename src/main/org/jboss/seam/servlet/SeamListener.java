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

import org.apache.myfaces.context.servlet.ServletExternalContextImpl;
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
      log.info("Welcome to Seam 1.0 beta 1");
      
      // TODO: Need for a portletContextListener, portletSessionListener
      new Initialization( new ServletExternalContextImpl(event.getServletContext(), null, null )).init();
   }

   public void contextDestroyed(ServletContextEvent event) {
      Lifecycle.endApplication( new ServletExternalContextImpl(event.getServletContext(), null, null) );
   }

   public void sessionCreated(HttpSessionEvent event) {}

   public void sessionDestroyed(HttpSessionEvent event) {
      Lifecycle.endSession( new ServletExternalContextImpl(event.getSession().getServletContext(), null, null) );
   }

}

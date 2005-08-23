//$Id$
package org.jboss.seam.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.jboss.logging.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.Seam;
import org.jboss.seam.components.Components;
import org.jboss.seam.components.ManagedPersistenceContext;
import org.jboss.seam.components.Settings;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.WebApplicationContext;

public class SeamServletContextListener implements ServletContextListener
{

   private static final Logger log = Logger.getLogger(SeamServletContextListener.class);
   
   public void contextInitialized(ServletContextEvent event)
   {
      log.info("initializing Seam");
      Context context = new WebApplicationContext( event.getServletContext() );
      
      Settings settings = new Settings();
      settings.init( event.getServletContext() );
      context.set( Seam.getComponentName(Settings.class), settings );
      
      Components components = new Components();
      addComponents(settings, components);
      context.set( Seam.getComponentName(Components.class), components );
      log.info("done initializing Seam");
   }

   protected void addComponents(Settings settings, Components components)
   {
      components.addComponent(Settings.class);
      components.addComponent(Components.class);
      for ( String className : settings.getComponentClassNames() )
      {
         components.addComponent(className);
      }
      for ( String unitName : settings.getPersistenceUnitNames() )
      {
         components.addComponent( unitName, new Component(ManagedPersistenceContext.class, unitName) );
      }
      for ( String sfName : settings.getSessionFactoryNames() )
      {
         components.addComponent( sfName, new Component(ManagedPersistenceContext.class, sfName) );
      }
   }

   public void contextDestroyed(ServletContextEvent event) { }

}

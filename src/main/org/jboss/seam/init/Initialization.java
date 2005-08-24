//$Id$
package org.jboss.seam.init;

import javax.servlet.ServletContext;

import org.jboss.logging.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.Seam;
import org.jboss.seam.components.Components;
import org.jboss.seam.components.ManagedPersistenceContext;
import org.jboss.seam.components.Settings;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.WebApplicationContext;

public class Initialization
{
   private static final Logger log = Logger.getLogger(Seam.class);

   public Initialization init(ServletContext servletContext)
   {
      log.info("initializing Seam");
      Context context = new WebApplicationContext( servletContext );
      
      Settings settings = new Settings();
      settings.init( servletContext );
      context.set( Seam.getComponentName(Settings.class), settings );
      
      Components components = new Components();
      addComponents(settings, components);
      context.set( Seam.getComponentName(Components.class), components );
      log.info("done initializing Seam");
      return this;
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

}

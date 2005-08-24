//$Id$
package org.jboss.seam.init;

import javax.servlet.ServletContext;

import org.jboss.logging.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.Seam;
import org.jboss.seam.components.ComponentManager;
import org.jboss.seam.components.ConversationManager;
import org.jboss.seam.components.ManagedPersistenceContext;
import org.jboss.seam.components.Settings;
import org.jboss.seam.components.ManagedJbpmSession;
import org.jboss.seam.components.ManagedHibernateSession;
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
      
      ComponentManager componentManager = new ComponentManager();
      addComponents(settings, componentManager);
      context.set( Seam.getComponentName(ComponentManager.class), componentManager );
      log.info("done initializing Seam");
      return this;
   }

   protected void addComponents(Settings settings, ComponentManager componentManager)
   {
      componentManager.addComponent(Settings.class);
      componentManager.addComponent(ComponentManager.class);
      componentManager.addComponent(ConversationManager.class);
      for ( String className : settings.getComponentClassNames() )
      {
         componentManager.addComponent(className);
      }
      for ( String unitName : settings.getPersistenceUnitNames() )
      {
         componentManager.addComponent( unitName, new Component(ManagedPersistenceContext.class, unitName) );
      }
      for ( String sfName : settings.getSessionFactoryNames() )
      {
         componentManager.addComponent( sfName, new Component(ManagedHibernateSession.class, sfName) );
      }
      for ( String jbpmSfName : settings.getJbpmSessionFactoryNames() )
      {
         componentManager.addComponent( jbpmSfName, new Component(ManagedJbpmSession.class, jbpmSfName) );
      }
   }

}

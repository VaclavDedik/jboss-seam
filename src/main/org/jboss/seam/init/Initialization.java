//$Id$
package org.jboss.seam.init;

import javax.servlet.ServletContext;

import org.jboss.logging.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.Seam;
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
      
      addComponents(settings, context);
      log.info("done initializing Seam");
      return this;
   }

   protected void addComponents(Settings settings, Context context)
   {
      addComponent( Settings.class, context );
      addComponent( ConversationManager.class, context );
      for ( String className : settings.getComponentClassNames() )
      {
         try
         {
            addComponent( Class.forName(className), context );
         }
         catch (ClassNotFoundException cnfe)
         {
            throw new IllegalArgumentException("Component class not found: " + className, cnfe);
         }
      }
      for ( String unitName : settings.getPersistenceUnitNames() )
      {
         addComponent( unitName, ManagedPersistenceContext.class, context );
      }
      for ( String sfName : settings.getSessionFactoryNames() )
      {
         addComponent( sfName, ManagedHibernateSession.class, context );
      }
      for ( String jbpmSfName : settings.getJbpmSessionFactoryNames() )
      {
         addComponent( jbpmSfName, ManagedJbpmSession.class, context );
      }
   }
   
   protected void addComponent(String name, Class clazz, Context context)
   {
      context.set(name + ".component", new Component(clazz, name) );
   }
   protected void addComponent(Class clazz, Context context)
   {
      context.set( Seam.getComponentName(clazz) + ".component", new Component(clazz) );
   }

}

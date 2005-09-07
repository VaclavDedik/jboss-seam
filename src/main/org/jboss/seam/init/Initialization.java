//$Id$
package org.jboss.seam.init;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.jboss.logging.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.Seam;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.Init;
import org.jboss.seam.core.ManagedHibernateSession;
import org.jboss.seam.core.ManagedJbpmSession;
import org.jboss.seam.core.ManagedPersistenceContext;
import org.jboss.seam.core.Manager;

public class Initialization
{
   private static final Logger log = Logger.getLogger(Seam.class);

   private Map<String, String> properties = new HashMap<String, String>();
   private ServletContext servletContext;

   public Initialization(ServletContext servletContext)
   {
      this.servletContext = servletContext;
      initPropertiesFromServletContext();
      initPropertiesFromResource();
   }

   public Initialization setProperty(String name, String value)
   {
      properties.put(name, value);
      return this;
   }

   public Initialization init()
   {
      log.info("initializing Seam");
      Lifecycle.beginInitialization(servletContext);
      Contexts.getApplicationContext().set(Component.PROPERTIES, properties);
      addComponents();
      Lifecycle.endInitialization();
      log.info("done initializing Seam");
      return this;
   }

   private void initPropertiesFromServletContext()
   {
      Enumeration paramNames = servletContext.getInitParameterNames();
      while (paramNames.hasMoreElements())
      {
         String name = (String) paramNames.nextElement();
         properties.put(name, servletContext.getInitParameter(name));
      }
   }

   private void initPropertiesFromResource()
   {
      InputStream stream = Seam.class.getResourceAsStream("/seam.properties");
      if (stream!=null)
      {
         Properties props = new Properties();
         try
         {
            props.load(stream);
         }
         catch (IOException ioe)
         {
            log.error("Could not read seam.properties", ioe);
         }
         ( (Map) properties ).putAll(props);
      }
      //( (Map) properties ).putAll( System.getProperties() );
   }

   protected void addComponents()
   {
      Context context = Contexts.getApplicationContext();

      addComponent( Init.class, context );
      addComponent( Manager.class, context );

      Init settings = (Init) Component.getInstance(Init.class, true);

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

      if ( settings.getJbpmSessionFactoryName() != null )
      {
         addComponent(
                 Seam.getComponentName( ManagedJbpmSession.class ),
                 ManagedJbpmSession.class,
                 context
         );
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

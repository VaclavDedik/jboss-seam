//$Id$
package org.jboss.seam.init;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.jboss.logging.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.Seam;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.Manager;
import org.jboss.seam.core.ManagedHibernateSession;
import org.jboss.seam.core.ManagedJbpmSession;
import org.jboss.seam.core.ManagedPersistenceContext;
import org.jboss.seam.core.Init;

public class Initialization
{
   private static final Logger log = Logger.getLogger(Seam.class);

   public Initialization init(ServletContext servletContext)
   {
      log.info("initializing Seam");
      Lifecycle.beginInitialization(servletContext);
      Map<String, String> properties = new HashMap<String, String>();
      initPropertiesFromServletContext(servletContext, properties);
      Contexts.getApplicationContext().set(Component.PROPERTIES, properties);
      addComponents();
      log.info("done initializing Seam");
      Lifecycle.endInitialization();
      return this;
   }

   private void initPropertiesFromServletContext(ServletContext servletContext, Map<String, String> properties)
   {
      Enumeration paramNames = servletContext.getInitParameterNames();
      while (paramNames.hasMoreElements())
      {
         String name = (String) paramNames.nextElement();
         properties.put(name, servletContext.getInitParameter(name));
      }
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

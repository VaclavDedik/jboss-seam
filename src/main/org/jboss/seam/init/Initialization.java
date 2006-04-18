/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.init;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Role;
import org.jboss.seam.annotations.Roles;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.Actor;
import org.jboss.seam.core.ApplicationContext;
import org.jboss.seam.core.BusinessProcessContext;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.ConversationContext;
import org.jboss.seam.core.ConversationList;
import org.jboss.seam.core.ConversationStack;
import org.jboss.seam.core.EventContext;
import org.jboss.seam.core.Events;
import org.jboss.seam.core.FacesContext;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.core.HttpError;
import org.jboss.seam.core.Init;
import org.jboss.seam.core.IsUserInRole;
import org.jboss.seam.core.Locale;
import org.jboss.seam.core.LocaleSelector;
import org.jboss.seam.core.ManagedHibernateSession;
import org.jboss.seam.core.ManagedJbpmContext;
import org.jboss.seam.core.ManagedPersistenceContext;
import org.jboss.seam.core.Manager;
import org.jboss.seam.core.Messages;
import org.jboss.seam.core.PageContext;
import org.jboss.seam.core.Pageflow;
import org.jboss.seam.core.Pages;
import org.jboss.seam.core.PooledTask;
import org.jboss.seam.core.PooledTaskInstanceList;
import org.jboss.seam.core.Process;
import org.jboss.seam.core.ProcessInstance;
import org.jboss.seam.core.Redirect;
import org.jboss.seam.core.ResourceBundle;
import org.jboss.seam.core.SessionContext;
import org.jboss.seam.core.StatelessContext;
import org.jboss.seam.core.Switcher;
import org.jboss.seam.core.TaskInstance;
import org.jboss.seam.core.TaskInstanceList;
import org.jboss.seam.core.TaskInstanceListForType;
import org.jboss.seam.core.Transition;
import org.jboss.seam.core.UiComponent;
import org.jboss.seam.core.UserPrincipal;
import org.jboss.seam.debug.Introspector;
import org.jboss.seam.deployment.Scanner;
import org.jboss.seam.remoting.messaging.SubscriptionRegistry;
import org.jboss.seam.util.Naming;
import org.jboss.seam.util.Reflections;
import org.jboss.seam.util.Resources;
import org.jboss.seam.util.Transactions;

/**
 * @author Gavin King
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public class Initialization
{
   
   private static final Log log = LogFactory.getLog(Initialization.class);

   private Map<String, String> properties = new HashMap<String, String>();
   private ServletContext servletContext;
   private boolean isScannerEnabled = true;

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
      Enumeration params = servletContext.getInitParameterNames();
      while (params.hasMoreElements())
      {
         String name = (String) params.nextElement();
         properties.put(name, servletContext.getInitParameter(name));
      }
   }

   private void initPropertiesFromResource()
   {
      loadFromResource( properties, "/seam.properties" );

      Properties jndiProperties = new Properties();
      loadFromResource( jndiProperties, "/seam-jndi.properties" );
      Naming.setInitialContextProperties(jndiProperties);
   }

   private static void initUserTransactionName(Properties properties)
   {
      String userTransactionName = properties.getProperty("jta.UserTransaction");
      if (userTransactionName!=null) Transactions.setUserTransactionName( userTransactionName );
   }

   public static void loadFromResource(Map properties, String resource)
   {
      InputStream stream = Resources.getResourceAsStream(resource);
      if (stream!=null)
      {
         log.info("reading properties from: " + resource);
         Properties props = new Properties();
         try
         {
            props.load(stream);
         }
         catch (IOException ioe)
         {
            log.error("could not read " + resource, ioe);
         }
         properties.putAll(props);
         initUserTransactionName(props); //TODO: this is very fragile!!!
      }
      else
      {
         log.debug("not found: " + resource);
      }
   }

   protected void addComponents()
   {
      Context context = Contexts.getApplicationContext();

      addComponent( Init.class, context );
      addComponent( Pages.class, context);
      addComponent( Events.class, context);
      addComponent( Manager.class, context );
      addComponent( Switcher.class, context );
      addComponent( Redirect.class, context );
      addComponent( HttpError.class, context );
      addComponent( UserPrincipal.class, context );
      addComponent( IsUserInRole.class, context );
      addComponent( Conversation.class, context );
      addComponent( ConversationList.class, context );
      addComponent( ConversationStack.class, context );
      addComponent( FacesContext.class, context );
      addComponent( PageContext.class, context );
      addComponent( EventContext.class, context );
      addComponent( SessionContext.class, context );
      addComponent( StatelessContext.class, context );
      addComponent( ApplicationContext.class, context );
      addComponent( ConversationContext.class, context );
      addComponent( BusinessProcessContext.class, context );
      addComponent( Locale.class, context );
      addComponent( Messages.class, context );
      addComponent( FacesMessages.class, context);
      addComponent( ResourceBundle.class, context );
      addComponent( LocaleSelector.class, context );
      addComponent( UiComponent.class, context );
      addComponent( Introspector.class, context );
      addComponent( org.jboss.seam.debug.Contexts.class, context );
      addComponent( SubscriptionRegistry.class, context);

      Init init = (Init) Component.getInstance(Init.class, true);

      //TODO: move all this stuff into Init component?
      for ( String className : init.getComponentClasses() )
      {
         try
         {
            Class<Object> componentClass = Reflections.classForName(className);
            addComponent( componentClass, context );
            addComponentRoles(context, componentClass);
         }
         catch (ClassNotFoundException cnfe)
         {
            throw new IllegalArgumentException("Component class not found: " + className, cnfe);
         }
      }

      for ( String unitName : init.getManagedPersistenceContexts() )
      {
         addComponent( unitName, ManagedPersistenceContext.class, context );
      }

      for ( String sfName : init.getManagedSessions() )
      {
         addComponent( sfName, ManagedHibernateSession.class, context );
      }

      if ( init.isJbpmInstalled() )
      {
         addComponent( Actor.class, context);
         addComponent( Process.class, context );
         addComponent( Pageflow.class, context );
         addComponent( Transition.class, context);
         addComponent( PooledTask.class, context );
         addComponent( TaskInstance.class, context );
         addComponent( ProcessInstance.class, context );
         addComponent( TaskInstanceList.class, context );
         addComponent( PooledTaskInstanceList.class, context );
         addComponent( TaskInstanceListForType.class, context );
         addComponent( ManagedJbpmContext.class, context );
      }

      if (isScannerEnabled)
      {
         for ( Class clazz: new Scanner().getClasses() )
         {
            if ( clazz.isAnnotationPresent(Name.class) )
            {
               addComponent(clazz, context);
               addComponentRoles(context, clazz);
            }
         }
      }

   }

   private void addComponentRoles(Context context, Class<Object> componentClass) {
      if ( componentClass.isAnnotationPresent(Role.class) )
      {
         Role role = componentClass.getAnnotation(Role.class);
         ScopeType scope = Seam.getComponentRoleScope(componentClass, role);
         addComponent( role.name(), scope, componentClass, context );
      }
      if ( componentClass.isAnnotationPresent(Roles.class) )
      {
         Role[] roles =componentClass.getAnnotation(Roles.class).value();
         for (Role role: roles)
         {
            ScopeType scope = Seam.getComponentRoleScope(componentClass, role);
            addComponent( role.name(), scope, componentClass, context );
         }
      }
   }

   protected void addComponent(String name, ScopeType scope, Class clazz, Context context)
   {
      context.set( name + ".component", new Component(clazz, name, scope) );
   }

   protected void addComponent(String name, Class clazz, Context context)
   {
      context.set( name + ".component", new Component(clazz, name) );
   }

   protected void addComponent(Class clazz, Context context)
   {
      context.set( Seam.getComponentName(clazz) + ".component", new Component(clazz) );
   }

   public boolean isScannerEnabled()
   {
      return isScannerEnabled;
   }

   public Initialization setScannerEnabled(boolean isScannerEnabled)
   {
      this.isScannerEnabled = isScannerEnabled;
      return this;
   }

}

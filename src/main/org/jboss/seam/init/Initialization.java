/*
�* JBoss, Home of Professional Open Source
�*
�* Distributable under LGPL license.
�* See terms of license at gnu.org.
�*/
package org.jboss.seam.init;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
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
import org.jboss.seam.core.BusinessProcess;
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
import org.jboss.seam.core.Interpolator;
import org.jboss.seam.core.IsUserInRole;
import org.jboss.seam.core.Jbpm;
import org.jboss.seam.core.Locale;
import org.jboss.seam.core.LocaleSelector;
import org.jboss.seam.core.ManagedJbpmContext;
import org.jboss.seam.core.Manager;
import org.jboss.seam.core.Messages;
import org.jboss.seam.core.PageContext;
import org.jboss.seam.core.Pageflow;
import org.jboss.seam.core.Pages;
import org.jboss.seam.core.PojoCache;
import org.jboss.seam.core.PooledTask;
import org.jboss.seam.core.PooledTaskInstanceList;
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
import org.jboss.seam.jms.ManagedQueueSender;
import org.jboss.seam.jms.ManagedTopicPublisher;
import org.jboss.seam.jms.QueueConnection;
import org.jboss.seam.jms.QueueSession;
import org.jboss.seam.jms.TopicConnection;
import org.jboss.seam.jms.TopicSession;
import org.jboss.seam.remoting.messaging.SubscriptionRegistry;
import org.jboss.seam.theme.Theme;
import org.jboss.seam.theme.ThemeSelector;
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
   public static final String COMPONENT_SUFFIX = ".component";
   
   private static final Log log = LogFactory.getLog(Initialization.class);

   private Map<String, String> properties = new HashMap<String, String>();
   private ServletContext servletContext;
   private boolean isScannerEnabled = true;
   private Map<String, Class> components = new HashMap<String, Class>();

   public Initialization(ServletContext servletContext)
   {
      this.servletContext = servletContext;
      initPropertiesFromXml();
      initPropertiesFromServletContext();
      initPropertiesFromResource();
   }
   
   private void initPropertiesFromXml()
   {
      InputStream stream = Resources.getResourceAsStream("/WEB-INF/components.xml", servletContext);      
      if (stream==null)
      {
         log.info("no components.xml file found");
      }
      else
      {
         log.info("reading components.xml");
         try
         {
            Properties replacements = new Properties();
            InputStream replaceStream = Resources.getResourceAsStream("components.properties");
            if (replaceStream!=null) replacements.load( replaceStream );
            
            SAXReader saxReader = new SAXReader();
            saxReader.setMergeAdjacentText(true);
            Document doc = saxReader.read(stream);
            
            List<Element> elements = doc.getRootElement().elements("component");
            for (Element component: elements)
            {
               String installed = component.attributeValue("installed");
               if (installed==null || "true".equals( replace(installed, replacements) ) )
               {
                  installComponent(component, replacements);
               }
            }
         }
         catch (Exception e)
         {
            throw new RuntimeException("error while reading components.xml", e);
         }
      }
   }

   private String replace(String value, Properties replacements)
   {
      if ( value.startsWith("@") ) 
      {
         value = replacements.getProperty( value.substring(1, value.length()-1) );
      }
      return value;
   }

   private void installComponent(Element component, Properties replacements) throws ClassNotFoundException
   {
      String name = component.attributeValue("name");
      String className = component.attributeValue("class");
      if (className!=null)
      {
         Class<?> clazz = Reflections.classForName(className);
         if (name==null)
         {
            name = clazz.getAnnotation(Name.class).value();
         }
         components.put(name, clazz);
      }
      else if (name==null)
      {
         throw new IllegalArgumentException("must specify either class or name in components.xml");
      }
         
      
      List<Element> props = component.elements("property");
      for( Element prop: props )
      {
         String propName = name + '.' + prop.attributeValue("name");
         String value = prop.getTextTrim();
         properties.put( propName, replace(value, replacements) );
      }
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
      loadFromResource( jndiProperties, "/jndi.properties" );
      loadFromResource( jndiProperties, "/seam-jndi.properties" );
      Naming.setInitialContextProperties(jndiProperties);
   }

   private static void initUserTransactionName(Properties properties)
   {
      String userTransactionName = properties.getProperty("jta.UserTransaction");
      if (userTransactionName!=null) Transactions.setUserTransactionName( userTransactionName );
   }

   public void loadFromResource(Map properties, String resource)
   {
      InputStream stream = Resources.getResourceAsStream(resource, servletContext);
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
      
      //force instantiation of Init
      Init init = (Init) Component.getInstance(Init.class, ScopeType.APPLICATION, true);
      
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
      addComponent( Theme.class, context);
      addComponent( ThemeSelector.class, context);
      addComponent( Interpolator.class, context );
      addComponent( FacesMessages.class, context );
      addComponent( ResourceBundle.class, context );
      addComponent( LocaleSelector.class, context );
      addComponent( UiComponent.class, context );
      addComponent( SubscriptionRegistry.class, context );

      try
      {
         addComponent( PojoCache.class, context );
      }
      catch (NoClassDefFoundError ncdfe) {} //swallow

      if ( components.values().contains(Jbpm.class) )
      {
         init.setJbpmInstalled(true);
      }

      if ( init.isDebug() )
      {
         addComponent( Introspector.class, context );
         addComponent( org.jboss.seam.debug.Contexts.class, context );
      }

      if ( init.isJbpmInstalled() )
      {
         addComponent( Actor.class, context);
         addComponent( BusinessProcess.class, context );
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
      
      if ( components.values().contains(ManagedTopicPublisher.class) )
      {
         addComponent( TopicConnection.class, context );
         addComponent( TopicSession.class, context );
      }

      if ( components.values().contains(ManagedQueueSender.class) )
      {
         addComponent( QueueConnection.class, context );
         addComponent( QueueSession.class, context );
      }

      for ( Map.Entry<String, Class> component : components.entrySet() )
      {
         addComponent( component.getKey(), component.getValue(), context );
      }

      if (isScannerEnabled)
      {
         for ( Class clazz: new Scanner().getClasses() )
         {
            addComponent(clazz, context);
            addComponentRoles(context, clazz);
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
      checkDuplicates(name, context);
      context.set( name + COMPONENT_SUFFIX, new Component(clazz, name, scope) );
   }

   protected void addComponent(String name, Class clazz, Context context)
   {
      checkDuplicates(name, context);
      context.set( name + COMPONENT_SUFFIX, new Component(clazz, name) );
   }

   protected void addComponent(Class clazz, Context context)
   {
      String name = Seam.getComponentName(clazz);
      checkDuplicates(name, context);
      context.set( name + COMPONENT_SUFFIX, new Component(clazz) );
   }
   
   private void checkDuplicates(String componentName, Context context)
   {
      if (log.isWarnEnabled() && (context.get(componentName + COMPONENT_SUFFIX) != null))
      {
         log.warn("Component with name " + componentName + " has been previously registered and is being redefined.");
      }
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

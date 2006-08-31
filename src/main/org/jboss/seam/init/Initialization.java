/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.init;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

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
import org.jboss.seam.core.RenderParameters;
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
import org.jboss.seam.remoting.RemotingConfig;
import org.jboss.seam.remoting.messaging.SubscriptionRegistry;
import org.jboss.seam.theme.Theme;
import org.jboss.seam.theme.ThemeSelector;
import org.jboss.seam.util.Conversions;
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

   private Map<String, Conversions.PropertyValue> properties = new HashMap<String, Conversions.PropertyValue>();
   private ServletContext servletContext;
   private boolean isScannerEnabled = true;
   private List<ComponentDescriptor> componentDescriptors = new ArrayList<ComponentDescriptor>();
   private Set<Class> installedComponents = new HashSet<Class>();

   public Initialization(ServletContext servletContext)
   {
      this.servletContext = servletContext;
      initPropertiesFromXml();
      initPropertiesFromServletContext();
      initPropertiesFromResource();
      initJndiProperties();
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

            List<Element> componentElements = doc.getRootElement().elements("component");
            for (Element component: componentElements)
            {
               String installed = component.attributeValue("installed");
               if (installed==null || "true".equals( replace(installed, replacements) ) )
               {
                  installComponent(component, replacements);
               }
            }

            List<Element> factoryElements = doc.getRootElement().elements("factory");
            for (Element factory: factoryElements)
            {
               String scopeName = factory.attributeValue("scope");
               Init.instance().addFactory(
                     factory.attributeValue("name"),
                     factory.attributeValue("expression"),
                     scopeName==null ?
                           ScopeType.UNSPECIFIED :
                           ScopeType.valueOf( scopeName.toUpperCase() )
                  );
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
      String scopeName = component.attributeValue("scope");
      ScopeType scope = scopeName==null ? null : ScopeType.valueOf(scopeName);
      if (className!=null)
      {
         Class<?> clazz = Reflections.classForName(className);
         if (name==null)
         {
            name = clazz.getAnnotation(Name.class).value();
         }
         componentDescriptors.add( new ComponentDescriptor(name, clazz, scope) );
         installedComponents.add(clazz);
      }
      else if (name==null)
      {
         throw new IllegalArgumentException("must specify either class or name in components.xml");
      }

      List<Element> props = component.elements("property");
      for( Element prop: props )
      {
         String propName = prop.attributeValue("name");
         if (propName==null)
         {
            throw new IllegalArgumentException("no name for property of component: " + name);
         }
         String qualifiedPropName = name + '.' + propName;
         properties.put( qualifiedPropName, getPropertyValue(prop, qualifiedPropName, replacements) );
      }
   }

   private Conversions.PropertyValue getPropertyValue(Element prop, String propName, Properties replacements)
   {
      List<Element> keyElements = prop.elements("key");
      List<Element> valueElements = prop.elements("value");

      Conversions.PropertyValue propertyValue;
      if ( valueElements.isEmpty() && keyElements.isEmpty() )
      {
         propertyValue = new Conversions.FlatPropertyValue( trimmedText(prop, propName, replacements) );
      }
      else if ( keyElements.isEmpty() )
      {
         //a list-like structure
         int len = valueElements.size();
         String[] values = new String[len];
         for (int i=0; i<len; i++)
         {
            values[i] = trimmedText( valueElements.get(i), propName, replacements );
         }
         propertyValue = new Conversions.MultiPropertyValue(values);
      }
      else
      {
         //a map-like structure
         if ( valueElements.size()!=keyElements.size() )
         {
            throw new IllegalArgumentException("value elements must match key elements: " + propName);
         }
         Map<String, String> keyedValues = new HashMap<String, String>();
         for (int i=0; i<keyElements.size(); i++)
         {
            String key = trimmedText( keyElements.get(i), propName, replacements );
            String value = trimmedText( valueElements.get(i), propName, replacements );
            keyedValues.put(key, value);
         }
         propertyValue = new Conversions.AssociativePropertyValue(keyedValues);
      }
      return propertyValue;

   }

   private String trimmedText(Element element, String propName, Properties replacements)
   {
      String text = element.getTextTrim();
      if (text==null)
      {
         throw new IllegalArgumentException("property value must be specified in element body: " + propName);
      }
      return replace(text, replacements);
   }

   public Initialization setProperty(String name, Conversions.PropertyValue value)
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
         properties.put( name, new Conversions.FlatPropertyValue( servletContext.getInitParameter(name) ) );
     }
   }

   private void initPropertiesFromResource()
   {
      Properties props = loadFromResource("/seam.properties");
      for (Map.Entry me: props.entrySet())
      {
         properties.put( (String) me.getKey(), new Conversions.FlatPropertyValue( (String) me.getValue() ) );
      }
   }

   private void initJndiProperties()
   {
      Properties jndiProperties = new Properties();
      jndiProperties.putAll( loadFromResource("/jndi.properties") );
      jndiProperties.putAll( loadFromResource("/seam-jndi.properties") );
      Naming.setInitialContextProperties(jndiProperties);
   }

   private static void initUserTransactionName(Properties properties)
   {
      String userTransactionName = properties.getProperty("jta.UserTransaction");
      if (userTransactionName!=null) Transactions.setUserTransactionName( userTransactionName );
   }

   private Properties loadFromResource(String resource)
   {
      Properties props = new Properties();
      InputStream stream = Resources.getResourceAsStream(resource, servletContext);
      if (stream!=null)
      {
         log.info("reading properties from: " + resource);
         try
         {
            props.load(stream);
         }
         catch (IOException ioe)
         {
            log.error("could not read " + resource, ioe);
         }
         initUserTransactionName(props); //TODO: this is very fragile!!!
      }
      else
      {
         log.debug("not found: " + resource);
      }
      return props;
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
      addComponent( RenderParameters.class, context );
      addComponent( SubscriptionRegistry.class, context );
      addComponent( RemotingConfig.class, context );

     try
      {
         addComponent( PojoCache.class, context );
      }
      catch (NoClassDefFoundError ncdfe) {} //swallow

      if ( installedComponents.contains(Jbpm.class) )
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

      if ( installedComponents.contains(ManagedTopicPublisher.class) )
      {
         addComponent( TopicConnection.class, context );
         addComponent( TopicSession.class, context );
      }

      if ( installedComponents.contains(ManagedQueueSender.class) )
      {
         addComponent( QueueConnection.class, context );
         addComponent( QueueSession.class, context );
      }

      for ( ComponentDescriptor componentDescriptor : componentDescriptors )
      {
         addComponent( componentDescriptor, context );
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
         addComponent( new ComponentDescriptor( role.name(), componentClass, scope), context );
      }
      if ( componentClass.isAnnotationPresent(Roles.class) )
      {
         Role[] roles =componentClass.getAnnotation(Roles.class).value();
         for (Role role: roles)
         {
            ScopeType scope = Seam.getComponentRoleScope(componentClass, role);
            addComponent( new ComponentDescriptor( role.name(), componentClass, scope), context );
         }
      }
   }

   protected void addComponent(ComponentDescriptor descriptor, Context context)
   {
      String name = descriptor.getName();
      String componentName = name + COMPONENT_SUFFIX;

      if ( log.isWarnEnabled() && context.isSet(componentName) )
      {
         log.warn("Component has been previously installed and is being redefined: " + name);
      }

      Component component = new Component(
            descriptor.getComponentClass(),
            name,
            descriptor.getScope()
         );
      context.set(componentName, component);

   }

   protected void addComponent(Class clazz, Context context)
   {
      addComponent( new ComponentDescriptor(clazz), context );
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

   private static class ComponentDescriptor
   {
      private String name;
      private Class componentClass;
      private ScopeType scope;

      public ComponentDescriptor(String name, Class componentClass, ScopeType scope)
      {
         this.name = name;
         this.componentClass = componentClass;
         this.scope = scope;
      }
      public ComponentDescriptor(Class componentClass)
      {
         this.componentClass = componentClass;
      }

      public String getName()
      {
         return name==null ? Seam.getComponentName(componentClass) : name;
      }
      public ScopeType getScope()
      {
         return scope==null ? Seam.getComponentScope(componentClass) : scope;
      }
      public Class getComponentClass()
      {
         return componentClass;
      }
   }

}

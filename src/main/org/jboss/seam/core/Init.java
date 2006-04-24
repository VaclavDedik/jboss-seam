//$Id$
package org.jboss.seam.core;


import static org.jboss.seam.InterceptionType.NEVER;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;

/**
 * A Seam component that holds Seam configuration settings
 * 
 * @author Gavin King
 */
@Scope(ScopeType.APPLICATION)
@Intercept(NEVER)
@Name("org.jboss.seam.core.init")
public class Init
{
   private static final String NAME = Seam.getComponentName(Init.class);
   public static final String COMPONENT_CLASSES = NAME + ".componentClasses";
   public static final String MANAGED_PERSISTENCE_CONTEXTS = NAME + ".managedPersistenceContexts";
   public static final String MANAGED_SESSIONS = NAME + ".managedSessions";
   public static final String MANAGED_DATA_SOURCES = NAME + ".managedDataSources";
   public static final String JNDI_PATTERN = NAME + ".jndiPattern";
   
   private String[] managedPersistenceContexts = {};
   private String[] managedSessions = {};
   private String[] componentClasses = {};
   private boolean isClientSideConversations = false;
   private boolean jbpmInstalled;
   private String jndiPattern;
   
   private Map<String, FactoryMethod> factories = new HashMap<String, FactoryMethod>();
   private Map<String, List<ObserverMethod>> observers = new HashMap<String, List<ObserverMethod>>();

   public String[] getManagedPersistenceContexts()
   {
      return managedPersistenceContexts;
   }
   public void setManagedPersistenceContexts(String[] managedPersistenceContexts)
   {
      this.managedPersistenceContexts = managedPersistenceContexts;
   }

   public String[] getManagedSessions()
   {
      return managedSessions;
   }

   public void setManagedSessions(String[] managedSessions)
   {
      this.managedSessions = managedSessions;
   }

   public String[] getComponentClasses()
   {
      return componentClasses;
   }

   public void setComponentClasses(String[] componentClasses)
   {
      this.componentClasses = componentClasses;
      jbpmInstalled = false;
      for (String className: componentClasses)
      {
         if ( Jbpm.class.getName().equals(className) )
         {
            jbpmInstalled = true;
         }
      }
   }
   
   public static Init instance()
   {
      if ( !Contexts.isApplicationContextActive() )
      {
         throw new IllegalStateException("No active application scope");
      }
      Init init = (Init) Contexts.getApplicationContext().get(Init.class);
      //commented out because of some test cases:
      /*if (init==null)
      {
         throw new IllegalStateException("No Init exists");
      }*/
      return init;
   }
   
   public boolean isClientSideConversations()
   {
      return isClientSideConversations;
   }

   public void setClientSideConversations(boolean isClientSideConversations)
   {
      this.isClientSideConversations = isClientSideConversations;
   }
   
   public static class FactoryMethod {
	   public Method method;
	   public Component component;
	   FactoryMethod(Method method, Component component)
	   {
		   this.method = method;
		   this.component = component;
	   }
   }
   
   public FactoryMethod getFactory(String variable)
   {
      return factories.get(variable);
   }
   
   public void addFactoryMethod(String variable, Method method, Component component)
   {
	   factories.put( variable, new FactoryMethod(method, component) );
   }
   
   public static class ObserverMethod {
      public Method method;
      public Component component;
      ObserverMethod(Method method, Component component)
      {
         this.method = method;
         this.component = component;
      }
   }
   
   public List<ObserverMethod> getObservers(String eventType)
   {
      return observers.get(eventType);
   }
   
   public void addObserverMethod(String eventType, Method method, Component component)
   {
      List<ObserverMethod> observerList = observers.get(eventType);
      if (observerList==null)
      {
         observerList = new ArrayList<ObserverMethod>();
         observers.put(eventType, observerList);
      }
      observerList.add( new ObserverMethod(method, component) );
   }
   
   public boolean isJbpmInstalled()
   {
      return jbpmInstalled;
   }
   
   public String getJndiPattern() 
   {
      return jndiPattern;
   }
    
   public void setJndiPattern(String jndiPattern) 
   {
	   this.jndiPattern = jndiPattern;
   }

}

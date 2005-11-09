//$Id$
package org.jboss.seam.core;


import static org.jboss.seam.InterceptionType.NEVER;

import java.lang.reflect.Method;
import java.util.HashMap;
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
   public static final String JBPM_SESSION_FACTORY_NAME = NAME + ".jbpmSessionFactoryName";
   
   private String[] managedPersistenceContexts = {};
   private String[] managedSessions = {};
   private String jbpmSessionFactoryName;
   private String[] componentClasses = {};
   private String[] managedDataSources = {};
   private boolean isClientSideConversations = false;
   
   private Map<String, FactoryMethod> factories = new HashMap<String, FactoryMethod>();

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

   public String getJbpmSessionFactoryName()
   {
      return jbpmSessionFactoryName;
   }

   public void setJbpmSessionFactoryName(String jbpmSessionFactoryName)
   {
      this.jbpmSessionFactoryName = jbpmSessionFactoryName;
   }

   public String[] getComponentClasses()
   {
      return componentClasses;
   }

   public void setComponentClasses(String[] componentClasses)
   {
      this.componentClasses = componentClasses;
   }
   
   public static Init instance()
   {
      return (Init) Contexts.getApplicationContext().get(Init.class);
   }
   
   public String[] getManagedDataSources()
   {
      return managedDataSources;
   }
   
   public void setManagedDataSources(String[] datasourceNames)
   {
      this.managedDataSources = datasourceNames;
   }
   
   public boolean isClientSideConversations()
   {
      return isClientSideConversations;
   }

   public void setClientSideConversations(boolean isClientSideConversations)
   {
      this.isClientSideConversations = isClientSideConversations;
   }
   
   public FactoryMethod getFactory(String variable)
   {
	   return factories.get(variable);
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
   
   public void addFactoryMethod(String variable, Method method, Component component)
   {
	   factories.put( variable, new FactoryMethod(method, component) );
   }

}

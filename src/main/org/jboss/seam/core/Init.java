//$Id$
package org.jboss.seam.core;


import static org.jboss.seam.InterceptionType.NEVER;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
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
   
   private boolean isClientSideConversations = false;
   private boolean jbpmInstalled;
   private String jndiPattern;
   private boolean debug;
   private boolean myFacesLifecycleBug;
   private List<String> mutableComponentNames = new ArrayList<String>();
   
   private Map<String, List<ObserverMethod>> observers = new HashMap<String, List<ObserverMethod>>();
   private Map<String, FactoryMethod> factories = new HashMap<String, FactoryMethod>();
   private Map<String, FactoryMethodBinding> factoryMethodBindings = new HashMap<String, FactoryMethodBinding>();
   
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
      public ScopeType scope;
	   FactoryMethod(Method method, Component component)
	   {
		   this.method = method;
		   this.component = component;
         scope = method.getAnnotation(org.jboss.seam.annotations.Factory.class).scope();
	   }
   }
   
   public static class FactoryMethodBinding {
      public MethodBinding methodBinding;
      public ScopeType scope;
      FactoryMethodBinding(MethodBinding methodBinding, ScopeType scope)
      {
         this.methodBinding = methodBinding;
         this.scope = scope;
      }
   }
   
   public FactoryMethod getFactory(String variable)
   {
      return factories.get(variable);
   }
   
   public FactoryMethodBinding getFactoryMethodBinding(String variable)
   {
      return factoryMethodBindings.get(variable);
   }
   
   public void addFactoryMethod(String variable, Method method, Component component)
   {
	   factories.put( variable, new FactoryMethod(method, component) );
   }
   
   public void addFactory(String variable, String methodBindingExpression, ScopeType scope)
   {
      MethodBinding methodBinding = FacesContext.getCurrentInstance().getApplication()
            .createMethodBinding(methodBindingExpression, null);
      factoryMethodBindings.put( variable, new FactoryMethodBinding(methodBinding, scope) );
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
   public boolean isDebug()
   {
      return debug;
   }
   public void setDebug(boolean debug)
   {
      this.debug = debug;
   }
   
   public boolean isMyFacesLifecycleBug()
   {
      return myFacesLifecycleBug;
   }
   
   public void setMyFacesLifecycleBug(boolean myFacesLifecycleBugExists)
   {
      this.myFacesLifecycleBug = myFacesLifecycleBugExists;
   }

   public void setJbpmInstalled(boolean jbpmInstalled)
   {
      this.jbpmInstalled = jbpmInstalled;
   }

   public List<String> getMutableComponentNames()
   {
      return mutableComponentNames;
   }

}

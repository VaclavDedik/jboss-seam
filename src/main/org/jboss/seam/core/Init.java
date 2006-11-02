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
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Expressions.MethodBinding;
import org.jboss.seam.core.Expressions.ValueBinding;
import org.jboss.seam.util.Transactions;

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
   private String userTransactionName;
   private String transactionManagerName;
   
   private Map<String, List<ObserverMethod>> observers = new HashMap<String, List<ObserverMethod>>();
   private Map<String, FactoryMethod> factories = new HashMap<String, FactoryMethod>();
   private Map<String, FactoryBinding> factoryMethodBindings = new HashMap<String, FactoryBinding>();
   private Map<String, FactoryBinding> factoryValueBindings = new HashMap<String, FactoryBinding>();
   
   @Create
   public void create()
   {
      if (transactionManagerName!=null)
      {
         Transactions.setTransactionManagerName(transactionManagerName);
      }
      if (userTransactionName!=null)
      {
         Transactions.setUserTransactionName(userTransactionName);
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
	   private Method method;
	   private Component component;
      private ScopeType scope;
      
	   FactoryMethod(Method method, Component component)
	   {
		   this.method = method;
		   this.component = component;
         scope = method.getAnnotation(org.jboss.seam.annotations.Factory.class).scope();
	   }
      
      public ScopeType getScope()
      {
         return scope;
      }
      public Component getComponent()
      {
         return component;
      }
      public Method getMethod()
      {
         return method;
      }
   }
   
   public static class FactoryBinding {
      private String expression;
      private ScopeType scope;
      
      FactoryBinding(String expression, ScopeType scope)
      {
         this.expression = expression;
         this.scope = scope;
      }
      
      public MethodBinding getMethodBinding()
      {
         //TODO: figure out some way to cache this!!
         return Expressions.instance().createMethodBinding(expression);
      }
      public ValueBinding getValueBinding()
      {
         //TODO: figure out some way to cache this!!
         return Expressions.instance().createValueBinding(expression);
      }
      public ScopeType getScope()
      {
         return scope;
      }
   }
   
   public FactoryMethod getFactory(String variable)
   {
      return factories.get(variable);
   }
   
   public FactoryBinding getFactoryMethodBinding(String variable)
   {
      return factoryMethodBindings.get(variable);
   }
   
   public FactoryBinding getFactoryValueBinding(String variable)
   {
      return factoryValueBindings.get(variable);
   }
   
   public void addFactoryMethod(String variable, Method method, Component component)
   {
	   factories.put( variable, new FactoryMethod(method, component) );
   }
   
   public void addFactoryMethodBinding(String variable, String methodBindingExpression, ScopeType scope)
   {
      factoryMethodBindings.put( variable, new FactoryBinding(methodBindingExpression, scope) );
   }
   
   public void addFactoryValueBinding(String variable, String valueBindingExpression, ScopeType scope)
   {
      factoryValueBindings.put( variable, new FactoryBinding(valueBindingExpression, scope) );
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

   /**
    * The JNDI name of the JTA TransactionManager
    */
   protected String getTransactionManagerName()
   {
      return transactionManagerName;
   }

   protected void setTransactionManagerName(String transactionManagerName)
   {
      this.transactionManagerName = transactionManagerName;
   }

   /**
    * The JNDI name of the JTA UserTransaction
    */
   protected String getUserTransactionName()
   {
      return userTransactionName;
   }

   protected void setUserTransactionName(String userTransactionName)
   {
      this.userTransactionName = userTransactionName;
   }

}

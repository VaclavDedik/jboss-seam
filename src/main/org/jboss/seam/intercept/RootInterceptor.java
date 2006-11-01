/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.intercept;

import java.io.Serializable;
import java.util.List;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.Component;
import org.jboss.seam.InterceptorType;
import org.jboss.seam.Seam;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;

/**
 * Abstract superclass of all controller interceptors
 * 
 * @author Gavin King
 */
public class RootInterceptor implements Serializable
{
   
   private static final Log log = LogFactory.getLog(RootInterceptor.class);
   
   private final InterceptorType type;
   private boolean isSeamComponent;
   private String componentName;
   private List<Object> userInterceptors;
   
   private transient Component component; //a cache of the Component reference for performance

   protected RootInterceptor(InterceptorType type)
   {
      this.type = type;
   }
   
   protected void init(Component component)
   {
      isSeamComponent = true;
      componentName = component.getName();
      userInterceptors = component.createUserInterceptors(type);
      this.component = component;
   }
   
   protected void initNonSeamComponent()
   {
      isSeamComponent = false;
   }
   
   protected void postConstruct(Object bean)
   {
      // initialize the bean instance
      if (isSeamComponent)
      {
         try
         {
            getComponent().initialize(bean);
         }
         catch (RuntimeException e)
         {
            throw e;
         }
         catch (Exception e)
         {
            throw new RuntimeException("exception initializing EJB component", e);
         }
      }      
   }

   protected void invokeAndHandle(InvocationContext invocation, EventType invocationType)
   {
      try
      {
         invoke(invocation, invocationType);
      }
      catch (RuntimeException e)
      {
         throw e;
      }
      catch (Exception e)
      {
         throw new RuntimeException("exception in EJB lifecycle callback", e);
      }
   }
   
   @AroundInvoke
   public Object aroundInvoke(InvocationContext invocation) throws Exception
   {
      return invoke(invocation, EventType.AROUND_INVOKE);
   }
   
   private Object invoke(InvocationContext invocation, EventType invocationType) throws Exception
   {
      if ( !isSeamComponent )
      {
         //not a Seam component
         return invocation.proceed();
      }
      else if ( Contexts.isEventContextActive() || Contexts.isApplicationContextActive() ) //not sure about the second bit (only needed at init time!)
      {
         //a Seam component, and Seam contexts exist
         return invokeInContexts(invocation, invocationType);
      }
      else
      {
         //if invoked outside of a set of Seam contexts,
         //set up temporary Seam EVENT and APPLICATION
         //contexts just for this call
         Lifecycle.beginCall();
         try
         {
            return invokeInContexts(invocation, invocationType);
         }
         finally
         {
            Lifecycle.endCall();
         }
      }
   }

   private Object invokeInContexts(InvocationContext invocation, EventType eventType) throws Exception
   {
      if ( isProcessInterceptors() )
      {
         if ( log.isTraceEnabled() ) 
         {
            log.trace( "intercepted: " + getInterceptionMessage(invocation, eventType) );
         }
         return new SeamInvocationContext( invocation, eventType, userInterceptors, getComponent().getInterceptors(type) ).proceed();
      }
      else {
         if ( log.isTraceEnabled() ) 
         {
            log.trace( "not intercepted: " + getInterceptionMessage(invocation, eventType) );
         }
         return invocation.proceed();
      }
   }

   private String getInterceptionMessage(InvocationContext invocation, EventType eventType)
   {
      return getComponent().getName() + '.' + 
            (eventType==EventType.AROUND_INVOKE ? invocation.getMethod().getName() : eventType );
   }

   private boolean isProcessInterceptors()
   {
      return isSeamComponent && getComponent().getInterceptionType().isActive();
   }
   
   protected Component getComponent()
   {
      if (isSeamComponent && component==null) 
      {
         component = Seam.componentForName(componentName);
      }
      return component;
   }

   protected String getComponentName()
   {
      return componentName;
   }
   
}

/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.ejb;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.Component;
import org.jboss.seam.InterceptorType;
import org.jboss.seam.Seam;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.interceptors.EventType;
import org.jboss.seam.interceptors.SeamInvocationContext;

/**
 * Interceptor for bijection and conversation scope management
 * for a session bean component
 * 
 * @author Gavin King
 * @version $Revision$
 */
public class SeamInterceptor implements Serializable
{
   
   private static final Log log = LogFactory.getLog(SeamInterceptor.class);
   
   private final InterceptorType type;
   private final Component component;
   
   /**
    * Called when instatiated by EJB container
    */
   public SeamInterceptor()
   {
      type = InterceptorType.SERVER;
      component = null;
   }
   
   /**
    * Called when instantiated by Seam
    */
   public SeamInterceptor(InterceptorType type, Component component)
   {
      this.type = type;
      this.component = component;
   }
   
   @PostConstruct
   public void postConstruct(InvocationContext invocation) throws Exception
   {
      Object bean = invocation.getTarget();
      if ( isSeamComponent(bean) )
      {
         getSeamComponent(bean).initialize(bean);
      }
      invoke(invocation, EventType.POST_CONSTRUCT);
   }
   
   @PreDestroy
   public void preDestroy(InvocationContext invocation) throws Exception
   {
      invoke(invocation, EventType.PRE_DESTORY);
   }
   
   @AroundInvoke
   public Object aroundInvoke(InvocationContext invocation) throws Exception
   {
      return invoke(invocation, EventType.AROUND_INVOKE);
   }
   
   private Object invoke(InvocationContext invocation, EventType invocationType) throws Exception
   {
      if ( !isSeamComponent( invocation.getTarget() ) )
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
      final Component component = getSeamComponent( invocation.getTarget() );
      if ( isProcessInterceptors(component) )
      {
         if ( log.isTraceEnabled() ) 
         {
            log.trace("intercepted: " + invocation.getMethod().getName());
         }
         return new SeamInvocationContext( invocation, eventType, component.getInterceptors(type) ).proceed();
      }
      else {
         if ( log.isTraceEnabled() ) 
         {
            log.trace("not intercepted: " + invocation.getMethod().getName());
         }
         return invocation.proceed();
      }
   }

   private boolean isProcessInterceptors(final Component component)
   {
      return component!=null && component.getInterceptionType().isActive();
   }

   private boolean isSeamComponent(Object bean)
   {
      return component!=null || Seam.getBeanClass( bean.getClass() )!=null;
   }
   
   private Component getSeamComponent(Object bean)
   {
      return component==null ? Component.forName( Seam.getComponentName( bean.getClass() ) ) : component;
   }
   
}

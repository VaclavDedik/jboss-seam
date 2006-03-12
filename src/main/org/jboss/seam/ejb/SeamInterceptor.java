/*
  * JBoss, Home of Professional Open Source
  *
  * Distributable under LGPL license.
  * See terms of license at gnu.org.
  */
package org.jboss.seam.ejb;

import java.io.Serializable;

import javax.ejb.AroundInvoke;
import javax.ejb.InvocationContext;

import org.jboss.logging.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.Seam;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
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
   
   private static final Logger log = Logger.getLogger(SeamInterceptor.class);
   
   @AroundInvoke
   public Object aroundInvoke(InvocationContext invocation) throws Exception
   {
      if ( Contexts.isEventContextActive() )
      {
         return aroundInvokeInContexts(invocation);
      }
      else
      {
         //if invoked outside of a set of Seam contexts,
         //set up temporary Seam EVENT and APPLICATION
         //contexts just for this call
         Lifecycle.beginCall();
         try
         {
            return aroundInvokeInContexts(invocation);
         }
         finally
         {
            Lifecycle.endCall();
         }
      }
   }
   
   public Object aroundInvokeInContexts(InvocationContext invocation) throws Exception
   {
      final Component component = getSeamComponent( invocation.getBean() );
      if ( isProcessInterceptors(component) )
      {
         if ( log.isTraceEnabled() ) 
         {
            log.trace("intercepted: " + invocation.getMethod().getName());
         }
         return new SeamInvocationContext(invocation, component).proceed();
      }
      else {
         if ( log.isTraceEnabled() ) 
         {
            log.trace("not intercepted: " + invocation.getMethod().getName());
         }
         //component.inject( invocation.getBean(), false );
         return invocation.proceed();
      }
   }

   private boolean isProcessInterceptors(final Component component)
   {
      return component!=null && component.getInterceptionType().isActive();
   }

   private Component getSeamComponent(Object bean)
   {
      String componentName = Seam.getComponentName( bean.getClass() );
      return Component.forName( componentName );
   }
   
}

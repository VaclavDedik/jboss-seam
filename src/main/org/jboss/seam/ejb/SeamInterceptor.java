/*
  * JBoss, Home of Professional Open Source
  *
  * Distributable under LGPL license.
  * See terms of license at gnu.org.
  */
package org.jboss.seam.ejb;

import javax.ejb.AroundInvoke;
import javax.ejb.InvocationContext;

import org.jboss.logging.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.Seam;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.finders.ComponentFinder;
import org.jboss.seam.interceptors.Interceptor;

/**
 * Interceptor for bijection and conversation scope management
 * for a session bean component
 * 
 * @author Gavin King
 * @version $Revision$
 */
public class SeamInterceptor
{
   
   private static final Logger log = Logger.getLogger(SeamInterceptor.class);
   
   private ComponentFinder componentFinder = new ComponentFinder();

   @AroundInvoke
   public Object aroundInvoke(InvocationContext invocation) throws Exception
   {
      if ( Contexts.isProcessing() )
      {
         log.debug("intercepted: " + invocation.getMethod().getName());
         
         final Component component = getSeamComponent( invocation.getBean() );
         
         for (Interceptor interceptor: component.getInterceptors())
         {
            Object result = interceptor.beforeInvoke(invocation);
            if (result!=null) return result;
         }

         Object result;
         try
         {
            log.info("invoking: " + invocation.getMethod().getName());
            result = invocation.proceed();
         } 
         catch (Exception exception)
         {
            for (Interceptor interceptor: component.getReverseInterceptors())
            {
               exception = interceptor.afterException(exception, invocation);
            }
            throw exception;
         }
         
         for (Interceptor interceptor: component.getReverseInterceptors())
         {
            result = interceptor.afterReturn(result, invocation);
         }
         
         return result;
      }
      else {
         log.debug("not intercepted: " + invocation.getMethod().getName());

         return invocation.proceed();
      }
   }

   private Component getSeamComponent(Object bean)
   {
      return componentFinder.getComponent( Seam.getComponentName( bean.getClass() ) );
   }
   
}

//$Id$
package org.jboss.seam.interceptors;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.jboss.seam.InterceptorType;
import org.jboss.seam.annotations.Interceptor;

/**
 * Serializes calls to a component.
 * 
 * @author Gavin King
 */
@Interceptor(type=InterceptorType.CLIENT)
public class SynchronizationInterceptor extends AbstractInterceptor
{
   
   private Semaphore semaphore = new Semaphore(1, true);
   
   @AroundInvoke
   public synchronized Object serialize(InvocationContext invocation) throws Exception
   {
      if ( semaphore.tryAcquire( getComponent().getTimeout(), TimeUnit.MILLISECONDS ) )
      {
         try
         {
            return invocation.proceed();
         }
         finally
         {
            semaphore.release();
         }
      }
      else
      {
         throw new IllegalStateException("could not acquire lock on @Synchronized component: " + getComponent().getName());
      }
   }


}

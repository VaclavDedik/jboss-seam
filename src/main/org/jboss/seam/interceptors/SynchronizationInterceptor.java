//$Id$
package org.jboss.seam.interceptors;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.jboss.seam.InterceptorType;
import org.jboss.seam.annotations.Interceptor;

/**
 * Serializes calls to a component.
 * 
 * @author Gavin King
 */
@Interceptor(stateless=true, type=InterceptorType.CLIENT)
public class SynchronizationInterceptor extends AbstractInterceptor
{
   
   @AroundInvoke
   public synchronized Object serialize(InvocationContext invocation) throws Exception
   {
      return invocation.proceed();
   }


}

package org.jboss.seam.interceptors;

import org.jboss.seam.annotations.AroundInvoke;
import org.jboss.seam.annotations.Interceptor;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.intercept.InvocationContext;

/**
 * Sets up the METHOD context and unproxies the SFSB 
 * for the duration of the call.
 * 
 * @author Gavin King
 *
 */
@Interceptor(stateless=true, around={BijectionInterceptor.class, EventInterceptor.class, SecurityInterceptor.class})
public class MethodContextInterceptor extends AbstractInterceptor
{
   @AroundInvoke
   public Object aroundInvoke(InvocationContext ctx) throws Exception
   {
      String name = getComponent().getName();
      Context outerMethodContext = Lifecycle.beginMethod();
      try
      {
         Contexts.getMethodContext().set( name, ctx.getTarget() );
         return ctx.proceed();
      }
      finally
      {
         Lifecycle.endMethod(outerMethodContext);
      }
   }
}

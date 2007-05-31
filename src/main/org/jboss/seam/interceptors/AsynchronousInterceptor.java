package org.jboss.seam.interceptors;

import org.jboss.seam.InterceptorType;
import org.jboss.seam.annotations.AroundInvoke;
import org.jboss.seam.annotations.Asynchronous;
import org.jboss.seam.annotations.Interceptor;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.AbstractDispatcher;
import org.jboss.seam.core.Dispatcher;
import org.jboss.seam.intercept.InvocationContext;

@Interceptor(stateless=true, type=InterceptorType.CLIENT)
public class AsynchronousInterceptor extends AbstractInterceptor
{
   private static final long serialVersionUID = 9194177339867853303L;
   
   @AroundInvoke
   public Object aroundInvoke(InvocationContext invocation) throws Exception
   {
      boolean scheduleAsync = invocation.getMethod().isAnnotationPresent(Asynchronous.class) && 
            !Contexts.getEventContext().isSet(AbstractDispatcher.EXECUTING_ASYNCHRONOUS_CALL);
      if (scheduleAsync)
      {
         Dispatcher dispatcher = AbstractDispatcher.instance();
         if (dispatcher==null)
         {
            throw new IllegalStateException("org.jboss.seam.core.dispatcher is not installed in components.xml");
         }
         Object timer = dispatcher.scheduleInvocation( invocation, getComponent() );
         //if the method returns a Timer, return it to the client
         return timer!=null && invocation.getMethod().getReturnType().isAssignableFrom( timer.getClass() ) ? timer : null;
      }
      else
      {
         return invocation.proceed();
      }
   }
}

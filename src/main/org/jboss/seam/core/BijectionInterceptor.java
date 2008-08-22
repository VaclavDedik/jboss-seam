//$Id$
package org.jboss.seam.core;

import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.intercept.AroundInvoke;
import org.jboss.seam.annotations.intercept.Interceptor;
import org.jboss.seam.intercept.AbstractInterceptor;
import org.jboss.seam.intercept.InvocationContext;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

/**
 * Before invoking the component, inject all dependencies. After
 * invoking, outject dependencies back into their context.
 * 
 * @author Gavin King
 */
@Interceptor
public class BijectionInterceptor extends AbstractInterceptor
{
   private static final long serialVersionUID = 4686458105931528659L;
   
   private static final LogProvider log = Logging.getLogProvider(BijectionInterceptor.class);
   
   private AtomicInteger reentrantCounter = new AtomicInteger();   
   
   @AroundInvoke
   public Object aroundInvoke(InvocationContext invocation) throws Exception
   {
      Component component = getComponent();
      try
      {
         if ( log.isTraceEnabled() && reentrantCounter.get() > 0 )
         {
            log.trace("reentrant call to component: " + getComponent().getName() );
         }
         
         reentrantCounter.incrementAndGet();            
         boolean enforceRequired = !component.isLifecycleMethod( invocation.getMethod() );
         component.inject( invocation.getTarget(), enforceRequired );
         Object result = invocation.proceed();            
         component.outject( invocation.getTarget(), enforceRequired );
         
         return result;
         
      }
      finally
      {
         if (reentrantCounter.decrementAndGet() == 0)
         {
            component.disinject( invocation.getTarget() );
         }
      }
   }

}

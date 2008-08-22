//$Id$
package org.jboss.seam.core;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.intercept.AroundInvoke;
import org.jboss.seam.annotations.intercept.Interceptor;
import org.jboss.seam.intercept.AbstractInterceptor;
import org.jboss.seam.intercept.InvocationContext;

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
   
   private Integer counter = 0;
      
   @AroundInvoke
   public Object aroundInvoke(InvocationContext invocation) throws Exception
   {
      try
      {
         synchronized (counter)
         {
            if (counter == 0)
            {
               Component component = getComponent();
               boolean enforceRequired = !component.isLifecycleMethod( invocation.getMethod() );
               component.inject( invocation.getTarget(), enforceRequired );
            }
            counter++;
         }
         
         Object result = invocation.proceed();
         
         if (counter == 1)
         {
            Component component = getComponent();
            boolean enforceRequired = !component.isLifecycleMethod( invocation.getMethod() );
            component.outject( invocation.getTarget(), enforceRequired );
         }
         return result;
      }
      finally
      {
         synchronized (counter)
         {
            if (counter == 1)
            {
               Component component = getComponent();
               component.disinject( invocation.getTarget() );
            }
            counter--;
            
         }
      }
   }
   
}

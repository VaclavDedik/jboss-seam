//$Id$
package org.jboss.seam.core;

import java.lang.reflect.Method;
import java.util.concurrent.locks.ReentrantLock;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.intercept.AroundInvoke;
import org.jboss.seam.annotations.intercept.Interceptor;
import org.jboss.seam.intercept.AbstractInterceptor;
import org.jboss.seam.intercept.InvocationContext;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.util.Reflections;

/**
 * Before invoking the component, inject all dependencies. After
 * invoking, outject dependencies back into their context.
 * 
 * @author Gavin King
 * @author Shane Bryzak
 */
@Interceptor
public class BijectionInterceptor extends AbstractInterceptor
{
   private static final long serialVersionUID = 4686458105931528659L;

   private static final LogProvider log = Logging.getLogProvider(BijectionInterceptor.class);
   
   private boolean injected;
   
   private boolean injecting;
   
   private int counter = 0;
   
   private ReentrantLock lock = new ReentrantLock();
         
   private String initialMethod;
   
   @AroundInvoke
   public Object aroundInvoke(InvocationContext invocation) throws Exception
   {
      Component component = getComponent();    
      Method method = invocation.getMethod();
      boolean enforceRequired = !component.isLifecycleMethod( method );      
      
      try
      {    
         lock.lock();
         try
         {
            if (!injected)
            {              
               if (injecting)
               {
                  log.warn("Injecting dependencies into " + component.getName() + " for the invocation of " 
                        + initialMethod + " caused the invocation of a reentrant method: " + Reflections.toString(method) 
                        + ".  Some injected dependencies may not be available for the duration of this method invocation.");
               }
               else
               {
                  injecting = true;
                  try
                  {
                     initialMethod = Reflections.toString(method);
                     component.inject(invocation.getTarget(), enforceRequired);
                  }
                  finally
                  {
                     injecting = false;
                     initialMethod = null;
                  }
               }
               injected = true;
            }
            
            counter++;
         }
         finally
         {
            lock.unlock();
         } 
                           
         Object result = invocation.proceed();
            
         lock.lock();
         try
         {
            counter--;
            
            if (counter == 0)
            {
               try
               {                     
                  component.outject( invocation.getTarget(), enforceRequired );
               }
               finally
               {
                  // Avoid an extra lock by disinjecting here instead of the finally block
                  if (injected)
                  {
                     injected = false;
                     component.disinject( invocation.getTarget() );
                  }
               }   
            }
         }
         finally
         {
            lock.unlock();
         }
         
         return result;
      }
      finally
      {            
         if (injected)
         {
            lock.lock();
            try
            {
               counter--;
               
               if (counter == 0)
               {
                  injected = false;
                  component.disinject( invocation.getTarget() );     
               }
            }
            finally
            {
               lock.unlock();
            }
         }
      }
   }
   
}

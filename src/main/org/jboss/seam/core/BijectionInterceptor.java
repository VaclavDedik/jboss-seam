//$Id$
package org.jboss.seam.core;

import java.util.concurrent.locks.ReentrantLock;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.intercept.AroundInvoke;
import org.jboss.seam.annotations.intercept.Interceptor;
import org.jboss.seam.intercept.AbstractInterceptor;
import org.jboss.seam.intercept.InvocationContext;

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
   
   private boolean injected;
   
   private boolean injecting;
   
   private int counter = 0;
   
   private ReentrantLock lock = new ReentrantLock();
         
   @AroundInvoke
   public Object aroundInvoke(InvocationContext invocation) throws Exception
   {
      Component component = getComponent();    
      boolean enforceRequired = !component.isLifecycleMethod( invocation.getMethod() );      
      
      try
      {    
         lock.lock();
         try
         {
            if (!injected)
            {              
               if (injecting == true)
               {
                  throw new CyclicDependencyException();
               }

               injecting = true;
               try
               {
                  component.inject(invocation.getTarget(), enforceRequired);
               }
               finally
               {
                  injecting = false;
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
      catch (CyclicDependencyException cyclicDependencyException)
      {
         cyclicDependencyException.addInvocation(getComponent().getName(), invocation.getMethod());
         throw cyclicDependencyException;
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

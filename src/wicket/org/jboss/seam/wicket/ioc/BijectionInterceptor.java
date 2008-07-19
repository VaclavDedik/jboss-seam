package org.jboss.seam.wicket.ioc;

import org.jboss.seam.wicket.WicketComponent;


public class BijectionInterceptor<T> implements StatelessInterceptor<T>
{

   public Object afterInvoke(InvocationContext<T> invocationContext, Object result)
   {
      invocationContext.getComponent().outject(invocationContext.getBean());
      invocationContext.getComponent().disinject(invocationContext.getBean());
      disinjectEnclosingInstances(invocationContext);
      return result;
   }

   public void beforeInvoke(InvocationContext<T> invocationContext)
   {
      try
      {
         invocationContext.getComponent().inject(invocationContext.getBean());
         injectEnclosingInstances(invocationContext);
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }
   
   public Exception handleException(InvocationContext<T> invocationContext, Exception exception)
   {
      return exception;
   }
   
   private static <T> void injectEnclosingInstances(InvocationContext<T> invocationContext)
   {
      InstrumentedComponent enclosingInstance = invocationContext.getInstrumentedComponent().getEnclosingInstance();
      while (enclosingInstance != null)
      {
         if (!enclosingInstance.getHandler().isReentrant())
         {
            WicketComponent.getInstance(enclosingInstance.getClass()).inject(enclosingInstance);
            enclosingInstance = enclosingInstance.getEnclosingInstance();
         }
         else
         {
            return;
         }
      }
   }
   
   private static <T> void disinjectEnclosingInstances(InvocationContext<T> invocationContext)
   {
      InstrumentedComponent enclosingInstance = invocationContext.getInstrumentedComponent().getEnclosingInstance();
      while (enclosingInstance != null)
      {
         if (!enclosingInstance.getHandler().isReentrant())
         {
            WicketComponent.getInstance(enclosingInstance.getClass()).disinject(enclosingInstance);
            enclosingInstance = enclosingInstance.getEnclosingInstance();
         }
         else
         {
            return;
         }
      }
   }

}

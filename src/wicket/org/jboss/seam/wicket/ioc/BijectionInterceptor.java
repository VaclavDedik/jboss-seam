package org.jboss.seam.wicket.ioc;

import org.jboss.seam.wicket.WicketComponent;


public class BijectionInterceptor<T> extends RootInterceptor<T>
{

   @Override
   public void afterInvoke(InvocationContext<T> invocationContext)
   {
      invocationContext.getComponent().outject(invocationContext.getBean());
      invocationContext.getComponent().disinject(invocationContext.getBean());
      disinjectEnclosingInstances(invocationContext);
   }

   @Override
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
   
   private static <T> void injectEnclosingInstances(InvocationContext<T> invocationContext)
   {
      InstrumentedComponent enclosingInstance = invocationContext.getInstrumentedComponent().getEnclosingInstance();
      while (enclosingInstance != null)
      {
         if (!enclosingInstance.getHandler().isCallInProgress())
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
         if (!enclosingInstance.getHandler().isCallInProgress())
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

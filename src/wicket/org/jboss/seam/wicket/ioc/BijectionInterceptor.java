package org.jboss.seam.wicket.ioc;


public class BijectionInterceptor<T> extends RootInterceptor<T>
{

   @Override
   public void afterInvoke(InvocationContext<T> invocationContext)
   {
      invocationContext.getComponent().outject(invocationContext.getBean());
      invocationContext.getComponent().disinject(invocationContext.getBean());
   }

   @Override
   public void beforeInvoke(InvocationContext<T> invocationContext)
   {
      try
      {
         invocationContext.getComponent().inject(invocationContext.getBean());
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

}

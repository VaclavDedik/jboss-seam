//$Id$
package org.jboss.seam.interceptors;

import javax.ejb.InvocationContext;

import org.jboss.seam.Component;

public class AbstractInterceptor<T> implements Interceptor<T>
{
   protected Component component;

   public void initialize(T annotation, Component component)
   {
      this.component = component;
   }

   public Object aroundInvoke(InvocationContext invocation) throws Exception
   {
      return invocation.proceed();
   }

   public void create(Object component)
   {
   }

   public void destroy(Object component)
   {
   }

}

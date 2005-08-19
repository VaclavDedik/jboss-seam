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

   public Object beforeInvoke(InvocationContext invocation)
   {
      return null;
   }

   public Object afterReturn(Object result, InvocationContext invocation)
   {
      return result;
   }

   public Exception afterException(Exception exception, InvocationContext invocation)
   {
      return exception;
   }

   public void create(Object component)
   {
   }

   public void destroy(Object component)
   {
   }

}

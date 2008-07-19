package org.jboss.seam.wicket.ioc;

import java.io.Serializable;


public interface Interceptor<T> extends Serializable
{

   public void beforeInvoke(InvocationContext<T> invocationContext);
   
   public Object afterInvoke(InvocationContext<T> invocationContext, Object result);
   
   public Exception handleException(InvocationContext<T> invocationContext, Exception exception);
     
}

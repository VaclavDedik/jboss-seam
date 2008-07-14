package org.jboss.seam.wicket.ioc;

import java.io.Serializable;


public abstract class RootInterceptor<T> implements Serializable
{

   public abstract void beforeInvoke(InvocationContext<T> invocationContext);
   
   public abstract void afterInvoke(InvocationContext<T> invocationContext);
     
}

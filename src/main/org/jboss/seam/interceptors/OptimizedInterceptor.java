package org.jboss.seam.interceptors;

import org.jboss.seam.intercept.InvocationContext;

public interface OptimizedInterceptor
{
   public Object aroundInvoke(InvocationContext ic) throws Exception;
}

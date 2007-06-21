package org.jboss.seam.interceptors;

import org.jboss.seam.intercept.InvocationContext;

/**
 * Interface that may be optionally implemented by an
 * interceptor, to make the stacktrace smaller.
 * 
 * @author Gavin King
 *
 */
public interface OptimizedInterceptor
{
   public Object aroundInvoke(InvocationContext ic) throws Exception;
}

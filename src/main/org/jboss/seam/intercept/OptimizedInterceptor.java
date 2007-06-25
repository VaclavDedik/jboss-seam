package org.jboss.seam.intercept;

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

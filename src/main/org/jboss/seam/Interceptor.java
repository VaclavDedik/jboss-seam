//$Id$
package org.jboss.seam;

import javax.ejb.InvocationContext;

/**
 * Interface implemented by seam component interceptors.
 * Warning: this interface will change when EJB3 introduces
 * stateful interceptors!
 * @author Gavin King
 */
public interface Interceptor<T>
{
   public void initialize(T annotation);
   public void beforeInvoke(InvocationContext invocation);
   public void afterReturn(Object result, InvocationContext invocation);
   public void afterException(Exception exception, InvocationContext invocation);
   public void create(Object component);
   public void destroy(Object component);
}

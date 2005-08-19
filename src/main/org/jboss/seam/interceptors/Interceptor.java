//$Id$
package org.jboss.seam.interceptors;

import javax.ejb.InvocationContext;

import org.jboss.seam.Component;

/**
 * Interface implemented by seam component interceptors.
 * Warning: this interface will change when EJB3 introduces
 * stateful interceptors!
 * @author Gavin King
 */
public interface Interceptor<T>
{
   public void initialize(T annotation, Component component);
   public Object beforeInvoke(InvocationContext invocation);
   public Object afterReturn(Object result, InvocationContext invocation);
   public Exception afterException(Exception exception, InvocationContext invocation);
   public void create(Object component);
   public void destroy(Object component);
}

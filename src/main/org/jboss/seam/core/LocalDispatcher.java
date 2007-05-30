package org.jboss.seam.core;

import java.util.Date;
import java.util.concurrent.Callable;

import javax.ejb.Local;

import org.jboss.seam.Component;
import org.jboss.seam.intercept.InvocationContext;

@Local
public interface LocalDispatcher<T>
{
   /**
    * Schedule an asynchronous method call.
    * @return some kind of timer object, or null
    */
   public T scheduleInvocation(InvocationContext invocation, Component component);
   /**
    * Schedule an asynchronous method call.
    * @return some kind of timer object, or null
    */
   public T scheduleEvent(String type, Long duration, Date expiration, Long intervalDuration, Object... parameters);
    
   public Object call(Callable task);
}

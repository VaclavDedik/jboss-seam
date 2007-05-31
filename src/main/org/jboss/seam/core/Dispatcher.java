package org.jboss.seam.core;

import org.jboss.seam.Component;
import org.jboss.seam.intercept.InvocationContext;

/**
 * Interface to be implemented by any strategy for dispatching
 * asynchronous method calls and asynchronous events.
 * 
 * @author Gavin King
 *
 * @param <T> the type of the timer object
 */
public interface Dispatcher<T, S>
{
   /**
    * Schedule an asynchronous method call
    * 
    * @return some kind of timer object, or null
    */
   public T scheduleInvocation(InvocationContext invocation, Component component);
   /**
    * Schedule a timed event
    * 
    * @return some kind of timer object, or null
    */
   public T scheduleTimedEvent(String type, S schedule, Object... parameters);
   
   /**
    * Schedule an asynchronous event
    * 
    * @return some kind of timer object, or null
    */
   public T scheduleAsynchronousEvent(String type, Object... parameters);
   
}

package org.jboss.seam.core;

import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.interceptor.Interceptors;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.ejb.SeamInterceptor;
import org.jboss.seam.intercept.InvocationContext;

/**
 * Dispatcher implementation that uses a java.util.concurrent
 * ThreadPoolExecutor.
 * 
 * @author Gavin King
 *
 */
@Scope(ScopeType.APPLICATION)
@Name("org.jboss.seam.core.dispatcher")
@Interceptors(SeamInterceptor.class)
@Install(value=false, precedence=BUILT_IN)
public class ThreadPoolDispatcher extends AbstractDispatcher<Future, Object>
{
   private ExecutorService executor = Executors.newCachedThreadPool();
    
   public Future scheduleTimedEvent(String type, Object schedule, Object... parameters)
   {
      throw new UnsupportedOperationException();
   }
   
   public Future scheduleAsynchronousEvent(String type, Object... parameters)
   {  
      final Asynchronous event = new AsynchronousEvent(type, parameters);
      return executor.submit( new Runnable() {
         public void run()
         {
            event.execute(null);
         }
      } );
   }
    
   public Future scheduleInvocation(InvocationContext invocation, Component component)
   {
      final Asynchronous call = new AsynchronousInvocation( 
               invocation.getMethod(),
               component.getName(),
               invocation.getParameters() 
            );
      return executor.submit( new Runnable() {
         public void run()
         {
            call.execute(null);
         }
      } );
   }
   
}

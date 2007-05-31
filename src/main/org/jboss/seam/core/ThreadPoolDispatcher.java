package org.jboss.seam.core;

import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ejb.Timer;
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
public class ThreadPoolDispatcher extends AbstractDispatcher
{
   private ExecutorService executor = Executors.newCachedThreadPool();
    
   public Object scheduleTimedEvent(String type, Object schedule, Object... parameters)
   {
      throw new UnsupportedOperationException();
   }
   
   public Object scheduleAsynchronousEvent(String type, Object... parameters)
   {  
      final Asynchronous event = new AsynchronousEvent(type, parameters);
      executor.execute( new Runnable() {
         public void run()
         {
            event.execute(null);
         }
      } );
      return null;
   }
    
   public Timer scheduleInvocation(InvocationContext invocation, Component component)
   {
      final Asynchronous call = new AsynchronousInvocation( 
               invocation.getMethod(),
               component.getName(),
               invocation.getParameters() 
            );
      executor.execute( new Runnable() {
         public void run()
         {
            call.execute(null);
         }
      } );
      return null;
   }
   
}

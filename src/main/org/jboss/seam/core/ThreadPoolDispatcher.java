package org.jboss.seam.core;

import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.interceptor.Interceptors;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.ejb.SeamInterceptor;
import org.jboss.seam.intercept.InvocationContext;

/**
 * Dispatcher implementation that uses a java.util.concurrent
 * ScheduledThreadPoolExecutor.
 * 
 * @author Gavin King
 *
 */
@Scope(ScopeType.APPLICATION)
@Name("org.jboss.seam.core.dispatcher")
@Interceptors(SeamInterceptor.class)
@Install(precedence=BUILT_IN)
public class ThreadPoolDispatcher extends AbstractDispatcher<Future, TimerSchedule>
{
   private int threadPoolSize = 10; 
   
   private ScheduledExecutorService executor = Executors.newScheduledThreadPool(threadPoolSize);
    
   public Future scheduleAsynchronousEvent(String type, Object... parameters)
   {  
      return executor.submit( new RunnableAsynchronous( new AsynchronousEvent(type, parameters) ) );
   }
    
   public Future scheduleTimedEvent(String type, TimerSchedule schedule, Object... parameters)
   {
      return scheduleWithExecutorService( schedule, new RunnableAsynchronous( new AsynchronousEvent(type, parameters) ) );
   }
   
   public Future scheduleInvocation(InvocationContext invocation, Component component)
   {
      return scheduleWithExecutorService( 
               createSchedule(invocation), 
               new RunnableAsynchronous( new AsynchronousInvocation(invocation, component) ) 
            );
   }
   
   private static long toDuration(Date expiration)
   {
      return expiration.getTime() - new Date().getTime();
   }
   
   private Future scheduleWithExecutorService(TimerSchedule schedule, Runnable runnable)
   {
      if ( schedule.getIntervalDuration()!=null )
      {
         if ( schedule.getExpiration()!=null )
         {
            return executor.scheduleAtFixedRate( runnable, toDuration( schedule.getExpiration() ), schedule.getIntervalDuration(), TimeUnit.MILLISECONDS );
         }
         else if ( schedule.getDuration()!=null )
         {
             return executor.scheduleAtFixedRate( runnable, schedule.getDuration(), schedule.getIntervalDuration(), TimeUnit.MILLISECONDS );
         }
         else
         {
            return executor.scheduleAtFixedRate( runnable, 0l, schedule.getIntervalDuration(), TimeUnit.MILLISECONDS );
         }
      }
      else if ( schedule.getExpiration()!=null )
      {
          return executor.schedule( runnable, toDuration( schedule.getExpiration() ), TimeUnit.MILLISECONDS );
      }
      else if ( schedule.getDuration()!=null )
      {
          return executor.schedule( runnable, schedule.getDuration(), TimeUnit.MILLISECONDS );
      }
      else
      {
         return executor.schedule(runnable, 0l, TimeUnit.MILLISECONDS);
      }
   }
   
   @Destroy
   public void destroy()
   {
      executor.shutdown();
      try
      {
         executor.awaitTermination(5, TimeUnit.SECONDS);
      }
      catch (InterruptedException ie)
      {
         
      }
   }
   
   static class RunnableAsynchronous implements Runnable
   {
      private AbstractDispatcher.Asynchronous async;
      
      RunnableAsynchronous(Asynchronous async)
      {
         this.async = async;
      }
      
      public void run()
      {
         async.execute(null);
      }
   }

   public int getThreadPoolSize()
   {
      return threadPoolSize;
   }

   public void setThreadPoolSize(int threadPoolSize)
   {
      this.threadPoolSize = threadPoolSize;
   }
   
}

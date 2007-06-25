package org.jboss.seam.async;

import java.lang.annotation.Annotation;
import java.util.Date;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.async.Duration;
import org.jboss.seam.annotations.async.Expiration;
import org.jboss.seam.annotations.async.FinalExpiration;
import org.jboss.seam.annotations.async.IntervalCron;
import org.jboss.seam.annotations.async.IntervalDuration;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.intercept.InvocationContext;
import org.jboss.seam.transaction.Transaction;

/**
 * Abstract Dispatcher implementation
 * 
 * @author Gavin King
 *
 */
public abstract class AbstractDispatcher<T, S> implements Dispatcher<T, S>
{
   
   public static final String EXECUTING_ASYNCHRONOUS_CALL = "org.jboss.seam.core.executingAsynchronousCall";
      
   public static Dispatcher instance()
   {
      if ( !Contexts.isApplicationContextActive() )
      {
         throw new IllegalStateException("no application context active");
      }
      return (Dispatcher) Component.getInstance("org.jboss.seam.async.dispatcher");         
   }
   
   public void scheduleTransactionSuccessEvent(String type, Object... parameters)
   {
      Transaction.instance().registerSynchronization( new TransactionSuccessEvent(type, parameters) );
   }

   public void scheduleTransactionCompletionEvent(String type, Object... parameters)
   {
      Transaction.instance().registerSynchronization( new TransactionCompletionEvent(type, parameters) );
   }

   // TODO: Throw exception when there are multiple interval params
   //       Make use of finalExpiration
   //       Make use of NthBusinessDay
   protected Schedule createSchedule(InvocationContext invocation)
   {
      Long duration = null;
      Date expiration = null;
      Date finalExpiration = null;

      Long intervalDuration = null;
      String cron = null;
      // NthBusinessDay intervalBusinessDay = null;

      Annotation[][] parameterAnnotations = invocation.getMethod().getParameterAnnotations();
      for ( int i=0; i<parameterAnnotations.length; i++ )
      {
         Annotation[] annotations = parameterAnnotations[i];
         for (Annotation annotation: annotations)
         {
            if ( annotation.annotationType().equals(Duration.class) )
            {
               duration = (Long) invocation.getParameters()[i];
            }
            else if ( annotation.annotationType().equals(IntervalDuration.class) )
            {
               intervalDuration = (Long) invocation.getParameters()[i];
            }
            else if ( annotation.annotationType().equals(Expiration.class) )
            {
               expiration = (Date) invocation.getParameters()[i];
            }
            else if ( annotation.annotationType().equals(FinalExpiration.class) )
            {
               finalExpiration = (Date) invocation.getParameters()[i];
            }
            else if ( annotation.annotationType().equals(IntervalCron.class) )
            {
               cron = (String) invocation.getParameters()[i];
            }
         }
      }
      
      if ( cron!=null ) 
      {
        return new CronSchedule(duration, expiration, cron, finalExpiration);
      } 
      else 
      {
        return new TimerSchedule(duration, expiration, intervalDuration, finalExpiration);
      }
   }
   
}

package org.jboss.seam.async;

import java.lang.annotation.Annotation;
import java.util.Date;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.async.Duration;
import org.jboss.seam.annotations.async.Expiration;
import org.jboss.seam.annotations.async.FinalExpiration;
import org.jboss.seam.annotations.async.IntervalCron;
import org.jboss.seam.annotations.async.IntervalDuration;
import org.jboss.seam.annotations.async.IntervalBusinessDay;
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

   protected Schedule createSchedule(InvocationContext invocation)
   {
      Long duration = null;
      Date expiration = null;
      Date finalExpiration = null;

      Long intervalDuration = null;
      String cron = null;
      NthBusinessDay intervalBusinessDay = null;
      
      int intervalParamCount = 0;

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
               intervalParamCount++;
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
               intervalParamCount++;
            }
            else if ( annotation.annotationType().equals(IntervalBusinessDay.class) )
            {
               intervalBusinessDay = (NthBusinessDay) invocation.getParameters()[i];
               intervalParamCount++;
            }
         }
      }
      
      if (intervalParamCount > 1) {
        throw new RuntimeException ("Cannot have more than one @Interval arguments in asynchrnous method");
      }
      
      if ( cron!=null ) 
      {
        return new CronSchedule(duration, expiration, cron, finalExpiration);
      } 
      else if (intervalBusinessDay != null)
      {
        return new NthBusinessDaySchedule(duration, expiration, intervalBusinessDay, finalExpiration);
      }
      else 
      {
        return new TimerSchedule(duration, expiration, intervalDuration, finalExpiration);
      }
   }
   
}

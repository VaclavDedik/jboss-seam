package org.jboss.seam.async;

import java.io.Serializable;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;

/**
 * Provides control over the Quartz Job.
 * 
 * @author Michael Yuan
 *
 */
public class QuartzTriggerHandle implements Serializable
{
   private String triggerName;
   
   // Hold a transient reference to the scheduler to allow control of the
   // scheduler outside of Seam contexts (useful in a testing context)
   private transient Scheduler scheduler;
     
   public QuartzTriggerHandle(String triggerName) 
   {
      this.triggerName = triggerName; 
      this.scheduler = QuartzDispatcher.instance().getScheduler();
   }

   public void cancel() throws SchedulerException
   {
      getScheduler().unscheduleJob(triggerName, null);
   }
   
   public void pause() throws SchedulerException
   {
      getScheduler().pauseTrigger(triggerName, null);  
   }
   
   public void resume() throws SchedulerException
   {
      getScheduler().resumeTrigger(triggerName, null);
   }
   
   private Scheduler getScheduler()
   {
       if (scheduler == null)
       {
           scheduler = QuartzDispatcher.instance().getScheduler();
       }
       return scheduler;
   }
   
}
  
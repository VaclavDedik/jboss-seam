package org.jboss.seam.async;

import java.io.Serializable;

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
     
   public QuartzTriggerHandle(String triggerName) 
   {
      this.triggerName = triggerName; 
   }

   public void cancel() throws SchedulerException
   {
      QuartzDispatcher.instance().getScheduler().unscheduleJob(triggerName, null);
   }
   
   public void pause() throws SchedulerException
   {
      QuartzDispatcher.instance().getScheduler().pauseTrigger(triggerName, null);  
   }
   
   public void resume() throws SchedulerException
   {
      QuartzDispatcher.instance().getScheduler().resumeTrigger(triggerName, null);
   }
}

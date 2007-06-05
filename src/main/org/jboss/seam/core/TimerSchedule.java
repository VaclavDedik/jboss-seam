/**
 * 
 */
package org.jboss.seam.core;

import java.io.Serializable;
import java.util.Date;

/**
 * A "schedule" for a timed event executed by
 * the EJB timer service or some other timer
 * service which supports delayed and/or periodic
 * timed events.
 * 
 * @author Gavin King
 *
 */
public class TimerSchedule implements Serializable
{
   private Long duration;
   private Date expiration;
   private Long intervalDuration;
   private String cron;
   
   Long getDuration()
   {
      return duration;
   }
   
   Date getExpiration()
   {
      return expiration;
   }
   
   Long getIntervalDuration()
   {
      return intervalDuration;
   }
   
   String getCron()
   {
      return cron;
   }
   
   /**
    * @param duration the delay before the event occurs
    */
   public TimerSchedule(Long duration)
   {
      this.duration = duration;
   }

   /**
    * @param expiration the datetime at which the event occurs
    */
   public TimerSchedule(Date expiration)
   {
      this.expiration = expiration;
   }

   /**
    * @param duration the delay before the first event occurs
    * @param intervalDuration the period between the events
    */
   public TimerSchedule(Long duration, Long intervalDuration)
   {
      this.duration = duration;
      this.intervalDuration = intervalDuration;
   }

   /**
    * @param duration the delay before the first event occurs
    * @param cron the unix cron string to control how the events are repeated
    */
   public TimerSchedule(Long duration, String cron)
   {
      this.duration = duration;
      this.cron = cron;
   }

   /**
    * @param expiration the datetime at which the first event occurs
    * @param intervalDuration the period between the events
    */
   public TimerSchedule(Date expiration, Long intervalDuration)
   {
      this.expiration = expiration;
      this.intervalDuration = intervalDuration;
   }

   /**
    * @param expiration the datetime at which the first event occurs
    * @param cron the unix cron string to control how the events are repeated
    */
   public TimerSchedule(Date expiration, String cron)
   {
      this.expiration = expiration;
      this.cron = cron;
   }

   TimerSchedule(Long duration, Date expiration, Long intervalDuration)
   {
      this.duration = duration;
      this.expiration = expiration;
      this.intervalDuration = intervalDuration;
   }

   TimerSchedule(Long duration, Date expiration, String cron)
   {
      this.duration = duration;
      this.expiration = expiration;
      this.cron = cron;
   }

   TimerSchedule() {}
   
   public static final TimerSchedule ONCE_IMMEDIATELY = new TimerSchedule();
   
}

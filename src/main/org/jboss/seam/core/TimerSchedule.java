package org.jboss.seam.core;

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
public class TimerSchedule extends Schedule
{
   private Long intervalDuration;
   
   Long getIntervalDuration()
   {
      return intervalDuration;
   }
   
   /**
    * @param duration the delay before the event occurs
    */
   public TimerSchedule(Long duration)
   {
      super(duration);
   }

   /**
    * @param expiration the datetime at which the event occurs
    */
   public TimerSchedule(Date expiration)
   {
      super(expiration);
   }

   /**
    * @param duration the delay before the first event occurs
    * @param intervalDuration the period between the events
    */
   public TimerSchedule(Long duration, Long intervalDuration)
   {
      super(duration);
      this.intervalDuration = intervalDuration;
   }

   /**
    * @param expiration the datetime at which the first event occurs
    * @param intervalDuration the period between the events
    */
   public TimerSchedule(Date expiration, Long intervalDuration)
   {
      super(expiration);
      this.intervalDuration = intervalDuration;
   }

   TimerSchedule(Long duration, Date expiration, Long intervalDuration)
   {
      super(duration, expiration);
      this.intervalDuration = intervalDuration;
   }

   TimerSchedule() {}
   
   public static final TimerSchedule ONCE_IMMEDIATELY = new TimerSchedule();
   
}

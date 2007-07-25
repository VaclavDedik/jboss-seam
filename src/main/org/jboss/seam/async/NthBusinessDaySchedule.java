package org.jboss.seam.async;

import java.util.Date;

/**
 * A "nth business day schedule" for a timed event executed by
 * the Quartz NthIncludedDayTrigger.
 * 
 * @author Michael Yuan
 *
 */
public class NthBusinessDaySchedule extends Schedule
{
   private NthBusinessDay nthBusinessDay;
   
   public NthBusinessDay getNthBusinessDay()
   {
      return nthBusinessDay;
   }
   
   public void setNthBusinessDay(NthBusinessDay nthBusinessDay)
   {
      this.nthBusinessDay = nthBusinessDay;
   }
   
   /**
    * @param duration the delay before the first event occurs
    * @param nthBusinessDay controls how the events are repeated on specified times on the Nth business day of the given interval (e.g., weekly, monthly, yearly)
    */
   public NthBusinessDaySchedule(Long duration, NthBusinessDay nthBusinessDay)
   {
      super(duration);
      this.nthBusinessDay = nthBusinessDay;
   }

   /**
    * @param expiration the datetime at which the first event occurs
    * @param nthBusinessDay controls how the events are repeated on specified times on the Nth business day of the given interval (e.g., weekly, monthly, yearly)
    */
   public NthBusinessDaySchedule(Date expiration, NthBusinessDay nthBusinessDay)
   {
      super(expiration);
      this.nthBusinessDay = nthBusinessDay;
   }

   public NthBusinessDaySchedule(Long duration, Date expiration, NthBusinessDay nthBusinessDay, Date finalExpiration)
   {
      super(duration, expiration, finalExpiration);
      this.nthBusinessDay = nthBusinessDay;
   }

   NthBusinessDaySchedule() {}
   
}

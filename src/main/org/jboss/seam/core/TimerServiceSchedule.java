/**
 * 
 */
package org.jboss.seam.core;

import java.io.Serializable;
import java.util.Date;

/**
 * A "schedule" for a timed event executed by
 * the EJB timer service.
 * 
 * @author Gavin King
 *
 */
public class TimerServiceSchedule implements Serializable
{
   private Long duration;
   private Date expiration;
   private Long intervalDuration;
   
   public Long getDuration()
   {
      return duration;
   }
   
   public Date getExpiration()
   {
      return expiration;
   }
   
   public Long getIntervalDuration()
   {
      return intervalDuration;
   }
   
   public TimerServiceSchedule(Long duration, Date expiration, Long intervalDuration)
   {
      this.duration = duration;
      this.expiration = expiration;
      this.intervalDuration = intervalDuration;
   }
   
}
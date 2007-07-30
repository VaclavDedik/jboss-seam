package org.jboss.seam.async;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import org.quartz.impl.calendar.WeeklyCalendar;
import org.quartz.impl.calendar.HolidayCalendar;

/**
 * The NthBusinessDay conf data -- used with @IntervalBusinessDay 
 * on @Asynchronous methods.
 * 
 * @author Michael Yuan
 *
 */
public class NthBusinessDay implements Serializable
{
      int n;
      String fireAtTime;
      List <Date> additionalHolidays;
      BusinessDayIntervalType interval;
      boolean excludeWeekends;
      boolean excludeUsFederalHolidays;
  
      public enum BusinessDayIntervalType { WEEKLY, MONTHLY, YEARLY } 

      public NthBusinessDay ()
      {
        n = 1;
        fireAtTime = "12:00";
        additionalHolidays = new ArrayList <Date> ();
        interval = BusinessDayIntervalType.WEEKLY;
        excludeWeekends = true;
        excludeUsFederalHolidays = true;
      }
      
      public NthBusinessDay (int n, String fireAtTime, BusinessDayIntervalType interval)
      {
        this.n = n;
        this.fireAtTime = fireAtTime;
        this.additionalHolidays = new ArrayList <Date> ();
        this.interval = interval;
        this.excludeWeekends = true;
        this.excludeUsFederalHolidays = true;
      }

      public NthBusinessDay (int n, String fireAtTime, List <Date> additionalHolidays, BusinessDayIntervalType interval, boolean excludeWeekends, boolean excludeUsFederalHolidays)
      {
        this.n = n;
        this.fireAtTime = fireAtTime;
        this.additionalHolidays = additionalHolidays;
        this.interval = interval;
        this.excludeWeekends = excludeWeekends;
        this.excludeUsFederalHolidays = excludeUsFederalHolidays;
      }

      public int getN () 
      {
        return n;
      }
      public void setN (int n) 
      {
        this.n = n;
      }

      public String getFireAtTime () 
      {
        return fireAtTime;
      }
      public void setFireAtTime (String fireAtTime)
      {
        this.fireAtTime = fireAtTime;
      }

      public List <Date> getAdditionalHolidays ()
      {
        return additionalHolidays;
      }
      public void setAdditionalHolidays (List <Date> additionalHolidays)
      {
        this.additionalHolidays = additionalHolidays;
      }

      public BusinessDayIntervalType getInterval ()
      {
        return interval;
      }
      public void setInterval (BusinessDayIntervalType interval)
      {
        this.interval = interval;
      }

      public boolean getExcludeWeekends ()
      {
        return excludeWeekends;
      }
      public void setExcludeWeekends (boolean excludeWeekends)
      {
        this.excludeWeekends = excludeWeekends;
      }
      
      public boolean getExcludeUsFederalHolidays ()
      {
        return excludeUsFederalHolidays;
      }
      public void setExcludeUsFederalHolidays (boolean excludeUsFederalHolidays)
      {
        this.excludeUsFederalHolidays = excludeUsFederalHolidays;
      }
      
      public HolidayCalendar getHolidayCalendar () 
      {
        HolidayCalendar holidays;
        
        if (excludeWeekends) {
          WeeklyCalendar wCal = new WeeklyCalendar ();
          holidays = new HolidayCalendar (wCal);
        } else {
          holidays = new HolidayCalendar ();
        }
        
        for (Date d : additionalHolidays) {
          holidays.addExcludedDate( d );
        }
        
        
        // US Federal Holiday based on http://www.opm.gov/fedhol/
        if (excludeUsFederalHolidays) {
          java.util.Calendar hCal = java.util.Calendar.getInstance();
          
          // Year 2007
          hCal.set(2007, java.util.Calendar.JANUARY, 1); // New Year
          holidays.addExcludedDate(hCal.getTime());  
          hCal.set(2007, java.util.Calendar.JANUARY, 15); // MLK
          holidays.addExcludedDate(hCal.getTime());          
          hCal.set(2007, java.util.Calendar.FEBRUARY, 19); // Washington
          holidays.addExcludedDate(hCal.getTime());
          hCal.set(2007, java.util.Calendar.MAY, 28); // Memorial
          holidays.addExcludedDate(hCal.getTime());
          hCal.set(2007, java.util.Calendar.JULY, 4); // Independence
          holidays.addExcludedDate(hCal.getTime());
          hCal.set(2007, java.util.Calendar.SEPTEMBER, 3); // Labor
          holidays.addExcludedDate(hCal.getTime());
          hCal.set(2007, java.util.Calendar.OCTOBER, 8); // Columbus
          holidays.addExcludedDate(hCal.getTime());
          hCal.set(2007, java.util.Calendar.NOVEMBER, 12); // Veterans
          holidays.addExcludedDate(hCal.getTime());
          hCal.set(2007, java.util.Calendar.NOVEMBER, 22); // Thanksgiving
          holidays.addExcludedDate(hCal.getTime());
          hCal.set(2007, java.util.Calendar.DECEMBER, 25); // Christmas
          holidays.addExcludedDate(hCal.getTime());
          
          // Year 2008
          hCal.set(2008, java.util.Calendar.JANUARY, 1); // New Year
          holidays.addExcludedDate(hCal.getTime());  
          hCal.set(2008, java.util.Calendar.JANUARY, 21); // MLK
          holidays.addExcludedDate(hCal.getTime());          
          hCal.set(2008, java.util.Calendar.FEBRUARY, 18); // Washington
          holidays.addExcludedDate(hCal.getTime());
          hCal.set(2008, java.util.Calendar.MAY, 26); // Memorial
          holidays.addExcludedDate(hCal.getTime());
          hCal.set(2008, java.util.Calendar.JULY, 4); // Independence
          holidays.addExcludedDate(hCal.getTime());
          hCal.set(2008, java.util.Calendar.SEPTEMBER, 1); // Labor
          holidays.addExcludedDate(hCal.getTime());
          hCal.set(2008, java.util.Calendar.OCTOBER, 13); // Columbus
          holidays.addExcludedDate(hCal.getTime());
          hCal.set(2008, java.util.Calendar.NOVEMBER, 11); // Veterans
          holidays.addExcludedDate(hCal.getTime());
          hCal.set(2008, java.util.Calendar.NOVEMBER, 27); // Thanksgiving
          holidays.addExcludedDate(hCal.getTime());
          hCal.set(2008, java.util.Calendar.DECEMBER, 25); // Christmas
          holidays.addExcludedDate(hCal.getTime());
          
          // Year 2009
          hCal.set(2009, java.util.Calendar.JANUARY, 1); // New Year
          holidays.addExcludedDate(hCal.getTime());  
          hCal.set(2009, java.util.Calendar.JANUARY, 19); // MLK
          holidays.addExcludedDate(hCal.getTime());          
          hCal.set(2009, java.util.Calendar.FEBRUARY, 16); // Washington
          holidays.addExcludedDate(hCal.getTime());
          hCal.set(2009, java.util.Calendar.MAY, 25); // Memorial
          holidays.addExcludedDate(hCal.getTime());
          hCal.set(2009, java.util.Calendar.JULY, 3); // Independence
          holidays.addExcludedDate(hCal.getTime());
          hCal.set(2009, java.util.Calendar.SEPTEMBER, 7); // Labor
          holidays.addExcludedDate(hCal.getTime());
          hCal.set(2009, java.util.Calendar.OCTOBER, 12); // Columbus
          holidays.addExcludedDate(hCal.getTime());
          hCal.set(2009, java.util.Calendar.NOVEMBER, 11); // Veterans
          holidays.addExcludedDate(hCal.getTime());
          hCal.set(2009, java.util.Calendar.NOVEMBER, 26); // Thanksgiving
          holidays.addExcludedDate(hCal.getTime());
          hCal.set(2009, java.util.Calendar.DECEMBER, 25); // Christmas
          holidays.addExcludedDate(hCal.getTime());
          
          // Year 2010
          hCal.set(2010, java.util.Calendar.JANUARY, 1); // New Year
          holidays.addExcludedDate(hCal.getTime());  
          hCal.set(2010, java.util.Calendar.JANUARY, 18); // MLK
          holidays.addExcludedDate(hCal.getTime());          
          hCal.set(2010, java.util.Calendar.FEBRUARY, 15); // Washington
          holidays.addExcludedDate(hCal.getTime());
          hCal.set(2010, java.util.Calendar.MAY, 31); // Memorial
          holidays.addExcludedDate(hCal.getTime());
          hCal.set(2010, java.util.Calendar.JULY, 5); // Independence
          holidays.addExcludedDate(hCal.getTime());
          hCal.set(2010, java.util.Calendar.SEPTEMBER, 6); // Labor
          holidays.addExcludedDate(hCal.getTime());
          hCal.set(2010, java.util.Calendar.OCTOBER, 11); // Columbus
          holidays.addExcludedDate(hCal.getTime());
          hCal.set(2010, java.util.Calendar.NOVEMBER, 11); // Veterans
          holidays.addExcludedDate(hCal.getTime());
          hCal.set(2010, java.util.Calendar.NOVEMBER, 25); // Thanksgiving
          holidays.addExcludedDate(hCal.getTime());
          hCal.set(2010, java.util.Calendar.DECEMBER, 24); // Christmas
          holidays.addExcludedDate(hCal.getTime());
        }
        return holidays;
      }

}

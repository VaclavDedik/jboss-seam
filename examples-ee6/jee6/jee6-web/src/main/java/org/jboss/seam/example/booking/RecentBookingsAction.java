package org.jboss.seam.example.booking;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.annotations.Startup;

@BypassInterceptors
@Scope(ScopeType.APPLICATION)
@Singleton
@Name("recentBookings")
@Startup
public class RecentBookingsAction
{
   private Map<Long, Date> latestBookingsMap; 
   
   @Observer("bookingConfirmed")
   @Lock(LockType.WRITE)
   public void onBookingConfirmed() {
      Booking booking = (Booking) Component.getInstance("booking");
      latestBookingsMap.put(booking.getHotel().getId(), new Date());
   }
   
   @Lock(LockType.READ)
   public Date getMostRecentBookingDate(Long hotelId) {
      return latestBookingsMap.get(hotelId);
   }
   
   @PostConstruct
   public void postConstruct() {
      latestBookingsMap = new HashMap<Long, Date>();
   }
}

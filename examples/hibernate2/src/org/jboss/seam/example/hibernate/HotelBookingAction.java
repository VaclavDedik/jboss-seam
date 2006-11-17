//$Id$
package org.jboss.seam.example.hibernate;

import static javax.persistence.PersistenceContextType.EXTENDED;

import java.util.Calendar;
import java.util.Date;
import org.hibernate.Session;

import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.core.Events;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.log.Log;

@Name("hotelBooking")
// @LoggedIn
public class HotelBookingAction {
   
   @In (create=true)
   private Session bookingDatabase;
   
   @In 
   private User user;
   
   @In(required=false) @Out
   private Hotel hotel;
   
   @In(required=false) 
   @Out(required=false)
   private Booking booking;
     
   @In
   private FacesMessages facesMessages;
      
   @In
   private Events events;
   
   @Logger 
   private Log log;
   
   @Begin
   public String selectHotel(Hotel selectedHotel)
   {
      hotel = (Hotel) bookingDatabase.merge(selectedHotel);
      return "hotel";
   }
   
   public String bookHotel()
   {      
      booking = new Booking(hotel, user);
      Calendar calendar = Calendar.getInstance();
      booking.setCheckinDate( calendar.getTime() );
      calendar.add(Calendar.DAY_OF_MONTH, 1);
      booking.setCheckoutDate( calendar.getTime() );
      
      return "book";
   }

   public String setBookingDetails()
   {
      if (booking==null || hotel==null) return "main";
      
      Calendar calendar = Calendar.getInstance();
      calendar.add(Calendar.DAY_OF_MONTH, -1);
      if ( booking.getCheckinDate().before( calendar.getTime() ) )
      {
         facesMessages.add("Check in date must be a future date");
         return null;
      }
      else if ( !booking.getCheckinDate().before( booking.getCheckoutDate() ) )
      {
         facesMessages.add("Check out date must be later than check in date");
         return null;
      }
      else
      {
         return "confirm";
      }
   }

   @End
   public String confirm()
   {
      if (booking==null || hotel==null) return "main";
      bookingDatabase.persist(booking);
      facesMessages.add("Thank you, #{user.name}, your confimation number for #{hotel.name} is #{booking.id}");
      log.info("New booking: #{booking.id} for #{user.username}");
      events.raiseEvent("bookingConfirmed");
      return "confirmed";
   }
   
   @End
   public String cancel()
   {
      return "main";
   }

}

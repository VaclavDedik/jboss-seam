//$Id$
package org.jboss.seam.example.booking;

import static javax.persistence.PersistenceContextType.EXTENDED;
import static org.jboss.seam.annotations.Outcome.REDISPLAY;

import java.io.Serializable;
import java.util.Calendar;

import javax.ejb.Interceptors;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.validator.Valid;
import org.jboss.logging.Logger;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Conversational;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.IfInvalid;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.ejb.SeamInterceptor;

@Stateful
@Name("hotelBooking")
@Interceptors(SeamInterceptor.class)
@Conversational(ifNotBegunOutcome="main")
@LoggedIn
public class HotelBookingAction implements HotelBooking, Serializable
{
   private static final Logger log = Logger.getLogger(HotelBooking.class);
   
   @PersistenceContext(type=EXTENDED)
   private EntityManager em;
   
   @In @Out
   private Hotel hotel;
   
   @In(required=false) 
   @Out(required=false)
   @Valid
   private Booking booking;
   
   @In
   private User user;
   
   @In
   private transient FacesContext facesContext;
   
   @In(create=true)
   private transient Conversation conversation;
   
   @In(required=false)
   private BookingList bookingList;
   
   @Begin(nested=true)
   public String bookHotel()
   {      
      booking = new Booking(hotel, user);
      Calendar calendar = Calendar.getInstance();
      booking.setCheckinDate( calendar.getTime() );
      calendar.add(Calendar.DAY_OF_MONTH, 1);
      booking.setCheckoutDate( calendar.getTime() );
      
      return conversation.switchableOutcome( "book", "Book hotel: " + hotel.getName() );
   }
   
   @IfInvalid(outcome=REDISPLAY)
   public String setBookingDetails()
   {
      if (booking==null || hotel==null) return "main";
      if ( !booking.getCheckinDate().before( booking.getCheckoutDate() ) )
      {
         log.info("invalid booking dates");
         FacesMessage facesMessage = new FacesMessage("Check out date must be later than check in date");
         facesContext.addMessage(null, facesMessage);
         return null;
      }
      else
      {
         log.info("valid booking");
         return conversation.switchableOutcome( "confirm", "Confirm booking: " + booking.getDescription() );
      }
   }
      
   @End
   public String confirm()
   {
      if (booking==null || hotel==null) return "main";
      em.persist(booking);
      if (bookingList!=null) bookingList.refresh();
      log.info("booking confirmed");
      return "confirmed";
   }
      
   @End
   public String cancel()
   {
      return "main";
   }
   
   @Destroy @Remove
   public void destroy() {
      log.info("destroyed");
   }

}
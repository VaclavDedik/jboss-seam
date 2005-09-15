//$Id$
package org.jboss.seam.example.noejb;

import static org.jboss.seam.ScopeType.CONVERSATION;
import static org.jboss.seam.annotations.Outcome.REDISPLAY;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.hibernate.Session;
import org.hibernate.validator.Valid;
import org.jboss.logging.Logger;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Conversational;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.IfInvalid;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;

@Name("hotelBooking")
@Scope(CONVERSATION)
@Conversational(ifNotBegunOutcome="main")
@LoggedIn
public class HotelBookingAction implements Serializable
{
   private static final Logger log = Logger.getLogger(HotelBookingAction.class);
   
   @In(create=true)
   private Session bookingDatabase;
   
   private String searchString;
   @Out
   private List<Hotel> hotels;
   
   @Out(required=false)
   private Hotel hotel;
   
   @In(required=false) 
   @Out(required=false)
   @Valid
   private Booking booking;
   
   @In
   private User user;
   
   int hotelIndex = 0;
   
   @In
   private FacesContext facesContext;
   
   public String getSearchString()
   {
      return searchString;
   }

   public void setSearchString(String searchString)
   {
      this.searchString = searchString;
   }

   @Begin
   public String find()
   {
      hotel = null;
      String searchPattern = searchString==null ? "%" : '%' + searchString.toLowerCase().replace('*', '%') + '%';
      hotels = bookingDatabase.createQuery("from Hotel where lower(city) like :search or lower(zip) like :search or lower(address) like :search")
            .setParameter("search", searchPattern)
            .setMaxResults(50)
            .list();
      
      log.info(hotels.size() + " hotels found");
      
      return "main";
   }
   
   public String selectHotel()
   {
      String hotelId = (String) facesContext.getExternalContext()
            .getRequestParameterMap().get("hotelId");
      log.info("hotelId: " + hotelId);
      if ( hotelId==null || hotels==null ) return "main";
      for (int i=0; i<hotels.size(); i++)
      {
         if ( hotels.get(i).getId().toString().equals(hotelId) )
         {
           hotelIndex=i;
           break;
         }
      }
      setHotel();
      return "selected";
   }

   public String nextHotel()
   {
      if ( hotelIndex<hotels.size()-1 )
      {
         ++hotelIndex;
         setHotel();
      }
      return null;
   }

   public String lastHotel()
   {
      if (hotelIndex>0)
      {
         --hotelIndex;
         setHotel();
      }
      return null;
   }

   private void setHotel()
   {
      hotel = hotels.get(hotelIndex);
      log.info( hotelIndex + "=>" + hotel );
   }
   
   public String bookHotel()
   {
      if (hotel==null) return "main";
      booking = new Booking(hotel, user);
      booking.setCheckinDate( new Date() );
      booking.setCheckoutDate( new Date() );
      return "book";
   }
   
   @IfInvalid(outcome=REDISPLAY)
   public String setBookingDetails()
   {
      if (booking==null || hotel==null) return "main";
      if ( !booking.getCheckinDate().before( booking.getCheckoutDate() ) )
      {
         log.info("invalid booking dates");
         FacesMessage facesMessage = new FacesMessage("Check in date must be later than check out date");
         facesContext.addMessage(null, facesMessage);
         return null;
      }
      else
      {
         log.info("valid booking");
         return "success";
      }
   }
      
   @End
   public String confirm()
   {
      if (booking==null || hotel==null) return "main";
      bookingDatabase.persist(booking);
      log.info("booking confirmed");
      return "confirmed";
   }

}

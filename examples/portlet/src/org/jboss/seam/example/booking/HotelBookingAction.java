//$Id$
package org.jboss.seam.example.booking;

import static javax.persistence.PersistenceContextType.EXTENDED;
import static org.jboss.seam.annotations.Outcome.REDISPLAY;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.ejb.Interceptor;
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
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelectionIndex;
import org.jboss.seam.ejb.SeamInterceptor;

@Stateful
@Name("hotelBooking")
@Interceptor(SeamInterceptor.class)
@Conversational(ifNotBegunOutcome="main")
@LoggedIn
public class HotelBookingAction implements HotelBooking, Serializable
{
   private static final Logger log = Logger.getLogger(HotelBooking.class);
   
   @PersistenceContext(type=EXTENDED)
   private EntityManager em;
   
   private String searchString;
   
   @DataModel
   private List<Hotel> hotels;
   @DataModelSelectionIndex
   private int hotelIndex;
   
   @Out(required=false)
   private Hotel hotel;
   
   @In(required=false) 
   @Out(required=false)
   @Valid
   private Booking booking;
   
   @In
   private User user;
   
   @In
   private transient FacesContext facesContext;
   
   @In(required=false)
   private BookingList bookingList;
   
   @Begin
   public String find()
   {
      hotel = null;
      String searchPattern = searchString==null ? "%" : '%' + searchString.toLowerCase().replace('*', '%') + '%';
      hotels = em.createQuery("from Hotel where lower(name) like :search or lower(city) like :search or lower(zip) like :search or lower(address) like :search")
            .setParameter("search", searchPattern)
            .setMaxResults(50)
            .getResultList();
      
      log.info(hotels.size() + " hotels found");
      
      return "main";
   }
   
   public String getSearchString()
   {
      return searchString;
   }

   public void setSearchString(String searchString)
   {
      this.searchString = searchString;
   }

   public String selectHotel()
   {
      if ( hotels==null ) return "main";
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
         FacesMessage facesMessage = new FacesMessage("Check out date must be later than check in date");
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
      em.persist(booking);
      if (bookingList!=null) bookingList.refresh();
      log.info("booking confirmed");
      return "confirmed";
   }
      
   @Destroy @Remove
   public void destroy() {
      log.info("destroyed");
   }
   
}

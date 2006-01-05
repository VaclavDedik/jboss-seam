//$Id$
package org.jboss.seam.example.booking;

import static javax.persistence.PersistenceContextType.EXTENDED;
import static org.jboss.seam.annotations.Outcome.REDISPLAY;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import javax.ejb.Interceptors;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.validator.Valid;
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
@Interceptors(SeamInterceptor.class)
@Conversational(ifNotBegunOutcome="main")
@LoggedIn
public class HotelBookingAction implements HotelBooking, Serializable
{
   
   @PersistenceContext(type=EXTENDED)
   private EntityManager em;
   
   private String searchString;
   private int pageSize = 10;
   
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
   
   @Begin(join=true)
   public String find()
   {
      hotel = null;
      String searchPattern = searchString==null ? "%" : '%' + searchString.toLowerCase().replace('*', '%') + '%';
      hotels = em.createQuery("from Hotel where lower(name) like :search or lower(city) like :search or lower(zip) like :search or lower(address) like :search")
            .setParameter("search", searchPattern)
            .setMaxResults(pageSize)
            .getResultList();
      
      return "main";
   }
   
   public int getPageSize() {
      return pageSize;
   }

   public void setPageSize(int pageSize) {
      this.pageSize = pageSize;
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

   @IfInvalid(outcome=REDISPLAY)
   public String setBookingDetails()
   {
      if (booking==null || hotel==null) return "main";
      if ( !booking.getCheckinDate().before( booking.getCheckoutDate() ) )
      {
         FacesMessage facesMessage = new FacesMessage("Check out date must be later than check in date");
         facesContext.addMessage(null, facesMessage);
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
      em.persist(booking);
      if (bookingList!=null) bookingList.refresh();
      return "confirmed";
   }
   
   @End
   public String clear()
   {
      hotels = null;
      hotel = null;
      return "main";
   }
   
   @Destroy @Remove
   public void destroy() {}

}
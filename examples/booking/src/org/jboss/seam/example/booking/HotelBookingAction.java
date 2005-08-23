//$Id$
package org.jboss.seam.example.booking;

import static org.jboss.seam.annotations.Outcome.REDISPLAY;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.ejb.Interceptor;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.persistence.EntityManager;

import org.hibernate.validator.Valid;
import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.logging.Logger;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Conversational;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.IfInvalid;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.ejb.SeamInterceptor;

@Stateful
@Name("hotelBooking")
@LocalBinding(jndiBinding="hotelBooking")
@Interceptor(SeamInterceptor.class)
@Conversational(ifNotBegunOutcome="main")
@LoggedIn
public class HotelBookingAction implements HotelBooking, Serializable
{
   private static final Logger log = Logger.getLogger(HotelBooking.class);
   
   @In(create=true)
   private EntityManager bookingDatabase;
   
   private String searchString;
   private List<Hotel> hotels;
   
   @Out(required=false)
   private Hotel hotel;
   
   @In(required=false) 
   @Out(required=false)
   @Valid
   private Booking booking;
   
   @In
   private User user;
   
   @Out
   private DataModel hotelsDataModel = new ListDataModel();
   int rowIndex = 0;
   
   @Begin
   public String find()
   {
      hotel = null;
      hotels = bookingDatabase.createQuery("from Hotel where lower(city) like :search or lower(zip) like :search or lower(address) like :search")
            .setParameter("search", '%' + searchString.toLowerCase().replace('*', '%') + '%')
            .setMaxResults(50)
            .getResultList();
      
      log.info(hotels.size() + " hotels found");
      
      hotelsDataModel.setWrappedData(hotels);
      
      return "main";
   }
   
   public String getSearchString()
   {
      return searchString;
   }

   public void setSearchString(String searchString)
   {
      this.searchString = searchString==null ? 
            "*" : searchString;
   }
   
   public String selectHotel()
   {
      rowIndex = hotelsDataModel.getRowIndex();
      setHotel();
      return "selected";
   }

   public String nextHotel()
   {
      if ( rowIndex<hotels.size()-1 )
      {
         hotelsDataModel.setRowIndex(++rowIndex);
         setHotel();
      }
      return null;
   }

   public String lastHotel()
   {
      if (rowIndex>0)
      {
         hotelsDataModel.setRowIndex(--rowIndex);
         setHotel();
      }
      return null;
   }

   private void setHotel()
   {
      hotel = (Hotel) hotelsDataModel.getRowData();
      log.info( rowIndex + "=>" + hotel );
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
         FacesContext.getCurrentInstance()
               .addMessage(null, new FacesMessage("Check in date must be later than check out date"));
         return "retry";
      }
      else
      {
         log.info("valid booking");
         return "success";
      }
   }
      
   @End @Remove 
   public String confirm()
   {
      if (booking==null || hotel==null) return "main";
      bookingDatabase.persist(booking);
      log.info("booking confirmed");
      return "confirmed";
   }
      
   @Destroy @Remove
   public void destroy() {}
}

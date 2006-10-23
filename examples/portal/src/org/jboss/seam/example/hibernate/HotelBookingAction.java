//$Id$
package org.jboss.seam.example.hibernate;

import static org.jboss.seam.ScopeType.CONVERSATION;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import org.hibernate.Session;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelectionIndex;
import org.jboss.seam.core.FacesMessages;

@Name("hotelBooking")
@Scope(CONVERSATION)
public class HotelBookingAction implements Serializable
{

   @In(create=true)
   private Session bookingDatabase;
   
   private String searchString;
   
   @DataModel
   private List<Hotel> hotels;
   @DataModelSelectionIndex
   int hotelIndex = 0;
   
   @Out(required=false)
   private Hotel hotel;
   
   @In(required=false) 
   @Out(required=false)
   private Booking booking;
   
   @In
   private User user;
   
   @In(required=false)
   private transient BookingListAction bookingList;
   
   @In(create=true)
   private transient FacesMessages facesMessages;

   public String getSearchString()
   {
      return searchString;
   }

   public void setSearchString(String searchString)
   {
      this.searchString = searchString;
   }

   @Begin(join=true)
   public String find()
   {
      hotel = null;
      String searchPattern = searchString==null ? "%" : '%' + searchString.toLowerCase().replace('*', '%') + '%';
      hotels = bookingDatabase.createQuery("from Hotel where lower(name) like :search or lower(city) like :search or lower(zip) like :search or lower(address) like :search")
            .setParameter("search", searchPattern)
            .setMaxResults(50)
            .list();
      
      return "main";
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
      return "browse";
   }

   public String lastHotel()
   {
      if (hotelIndex>0)
      {
         --hotelIndex;
         setHotel();
      }
       return "browse";
   }

   private void setHotel()
   {
      hotel = hotels.get(hotelIndex);
   }
   
   public String bookHotel()
   {
      if (hotel==null) return "main";
      
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
      
      if ( !booking.getCheckinDate().before( booking.getCheckoutDate() ) )
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
      if (bookingList!=null) bookingList.refresh();
      return "confirmed";
   }
   
   @End
   public String clear()
   {
      hotels = null;
      hotel = null;
      booking = null;
      return "main";
   }
   
}

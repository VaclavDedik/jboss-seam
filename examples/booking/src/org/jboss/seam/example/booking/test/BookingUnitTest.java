package org.jboss.seam.example.booking.test;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jboss.seam.core.Events;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.example.booking.Booking;
import org.jboss.seam.example.booking.Hotel;
import org.jboss.seam.example.booking.HotelBooking;
import org.jboss.seam.example.booking.HotelBookingAction;
import org.jboss.seam.example.booking.HotelSearching;
import org.jboss.seam.example.booking.HotelSearchingAction;
import org.jboss.seam.example.booking.User;
import org.jboss.seam.log.LogImpl;
import org.jboss.seam.mock.SeamTest;
import org.testng.annotations.Test;

public class BookingUnitTest extends SeamTest
{
   
   @Test
   public void testHotelSearching() throws Exception
   {
      EntityManagerFactory emf = Persistence.createEntityManagerFactory("bookingDatabase");

      EntityManager em = emf.createEntityManager();
      
      HotelSearching hs = new HotelSearchingAction();
      
      setField(hs, "em", em);
      
      hs.setSearchString("atlanta");
      hs.find();
      
      List<Hotel> hotels = (List<Hotel>) getField(hs, "hotels");
      assert hotels!=null;
      assert hotels.size()==3;
      
      em.close();
   }
   
   @Test
   public void testHotelBooking() throws Exception
   {
      EntityManagerFactory emf = Persistence.createEntityManagerFactory("bookingDatabase");
      final EntityManager em = emf.createEntityManager();
      
      HotelSearching hs = new HotelSearchingAction() {
         @Override
         public Hotel getSelectedHotel()
         {
            return em.getReference(Hotel.class, 1l);
         }
      };
      
      HotelBooking hb = new HotelBookingAction();
      
      setField(hb, "em", em);
      setField(hb, "hotelSearch", hs);
      //setField(hb, "user", em.getReference(User.class, "gavin"));
      setField(hb, "facesMessages", new FacesMessages());
      setField(hb, "events", new Events() { public void raiseEvent(String type) { assert "bookingConfirmed".equals(type); } } );
      setField(hb, "log", new LogImpl(HotelBookingAction.class));
      
      assert hb.selectHotel(hs.getSelectedHotel()).equals("hotel");

      User user = (User)em.getReference(User.class, "gavin");
      assert hb.bookHotel(user).equals("book");
      
      Booking booking = (Booking) getField(hb, "booking");
      assert booking!=null;
      assert booking.getHotel()!=null;
      assert booking.getUser()!=null;
      
      booking.setCreditCard("1234123412341234");
      booking.setCreditCardName("GAVIN A KING");
      
      assert hb.setBookingDetails().equals("confirm");

      getUserTransaction().begin();
      assert hb.confirm(user).equals("confirmed");
      getUserTransaction().commit();
      
      em.close();
   }

}

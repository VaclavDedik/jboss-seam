//$Id: BookingTest.java 5810 2007-07-16 06:46:47Z gavin $
package org.jboss.seam.example.booking.test;

import java.util.Calendar;
import java.util.Date;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.Component;
import org.jboss.seam.annotations.In;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.Manager;
import org.jboss.seam.example.booking.Booking;
import org.jboss.seam.example.booking.BookingListAction;
import org.jboss.seam.example.booking.Hotel;
import org.jboss.seam.example.booking.HotelBookingAction;
import org.jboss.seam.example.booking.HotelSearchingAction;
import org.jboss.seam.example.booking.RecentBookingsAction;
import org.jboss.seam.example.booking.User;
import org.jboss.seam.security.Identity;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class BookingTest
{
   @Deployment(name="BookingTest")
   @OverProtocol("Servlet 3.0") 
   public static Archive<?> createDeployment()
   {
      return Deployments.bookingDeployment();
   }

   @Before
   public void before() {
      Lifecycle.beginCall();
   }

   @After
   public void after() {
      Lifecycle.endCall();
   }

   @Test
   public void testBookHotel() throws Exception
   {
      Manager manager = Manager.instance();
      Identity identity = Identity.instance();
      HotelSearchingAction hotelSearch = (HotelSearchingAction)Component.getInstance("hotelSearch");
      HotelBookingAction hotelBooking = (HotelBookingAction)Component.getInstance("hotelBooking");
      BookingListAction bookingList = (BookingListAction)Component.getInstance("bookingList");

      manager.initializeTemporaryConversation();
      Contexts.getSessionContext().set("user", new User("Gavin King", "foobar", "gavin"));

      identity.setUsername("gavin");
      identity.setPassword("foobar");
      identity.login();

      hotelSearch.setSearchString("Union Square");
      hotelSearch.find();

      DataModel hotels = (DataModel) Contexts.getSessionContext().get("hotels");
      
      assertEquals(1, hotels.getRowCount());
      assertEquals("NY", ((Hotel) hotels.getRowData() ).getCity());
      assertEquals("Union Square", hotelSearch.getSearchString());
      assertFalse(manager.isLongRunningConversation());

      long hotelId = ((Hotel) hotels.getRowData() ).getId();
      RecentBookingsAction recentBookings = (RecentBookingsAction)Component.getInstance("recentBookings");
      assertNull(recentBookings.getMostRecentBookingDate(hotelId));

      hotels = (DataModel) Contexts.getSessionContext().get("hotels");
      assertEquals(1, hotels.getRowCount());
      hotelBooking.selectHotel( (Hotel) hotels.getRowData() );

      Hotel hotel = (Hotel) Contexts.getConversationContext().get("hotel");
      assertEquals("NY", hotel.getCity());
      assertEquals("10011", hotel.getZip());
      assertTrue(manager.isLongRunningConversation());

      hotelBooking.bookHotel();

      Booking booking = (Booking) Contexts.getConversationContext().get("booking");
      assertNotNull(booking.getUser());
      assertNotNull(booking.getHotel());
      assertNull(booking.getCreditCard());
      assertNull(booking.getCreditCardName());

      assertEquals(Contexts.getConversationContext().get("hotel"), booking.getHotel());
      assertEquals(Contexts.getSessionContext().get("user"), booking.getUser());
      assertTrue(Manager.instance().isLongRunningConversation());

      booking.setCreditCard("1234567891021234");
      booking.setCreditCardName("GAVIN KING");
      booking.setBeds(2);
      Date now = new Date();
      booking.setCheckinDate(now);
      booking.setCheckoutDate(now);

      hotelBooking.setBookingDetails();
      assertFalse(hotelBooking.isBookingValid());

      Calendar cal = Calendar.getInstance();
      cal.add(Calendar.DAY_OF_MONTH, 2);
      booking.setCheckoutDate(cal.getTime());

      hotelBooking.setBookingDetails();
      assertTrue(hotelBooking.isBookingValid());
      assertTrue(manager.isLongRunningConversation());

      hotelBooking.confirm();

      ListDataModel bookings = (ListDataModel) Component.getInstance("bookings");
      assertEquals(1, bookings.getRowCount());
      bookings.setRowIndex(0);
      booking = (Booking) bookings.getRowData();
      assertEquals("NY",  booking.getHotel().getCity());
      assertEquals("gavin",  booking.getUser().getUsername());
      assertFalse(manager.isLongRunningConversation());

      recentBookings = (RecentBookingsAction)Component.getInstance("recentBookings");
      assertNotNull(recentBookings.getMostRecentBookingDate(hotelId));

      bookings = (ListDataModel) Contexts.getSessionContext().get("bookings");
      bookings.setRowIndex(0);
      bookingList.cancel();

      bookings = (ListDataModel) Contexts.getSessionContext().get("bookings");
      assertEquals(0, bookings.getRowCount());
      assertFalse(manager.isLongRunningConversation());
   }

}

//$Id$
package org.jboss.seam.example.booking.test;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Manager;
import org.jboss.seam.example.booking.Booking;
import org.jboss.seam.example.booking.BookingList;
import org.jboss.seam.example.booking.Hotel;
import org.jboss.seam.example.booking.HotelBooking;
import org.jboss.seam.example.booking.HotelSearching;
import org.jboss.seam.example.booking.User;
import org.jboss.seam.mock.SeamTest;
import org.testng.annotations.Test;

public class BookingTest extends SeamTest
{
   
   @Test
   public void testBookHotel() throws Exception
   {
      
      new Script() {

         HotelSearching hotelSearch;
         
         @Override
         protected void applyRequestValues()
         {
            Contexts.getSessionContext().set("loggedIn", true);
            Contexts.getSessionContext().set("user", new User("Gavin King", "foobar", "gavin"));
         }

         @Override
         protected void updateModelValues() throws Exception
         {
            hotelSearch = (HotelSearching) Component.getInstance("hotelSearch", true);
            hotelSearch.setSearchString("Union Square");
         }

         @Override
         protected void invokeApplication()
         {
            String outcome = hotelSearch.find();
            assert "main".equals( outcome );
         }

         @Override
         protected void renderResponse()
         {
            DataModel hotels = (DataModel) Contexts.getSessionContext().get("hotels");
            assert hotels.getRowCount()==1;
            assert ( (Hotel) hotels.getRowData() ).getCity().equals("NY");
            assert "Union Square".equals( hotelSearch.getSearchString() );
            assert !Manager.instance().isLongRunningConversation();
         }
         
      }.run();
      
      String id = new Script() {

         HotelSearching hotelSearch;
         
         @Override
         protected void updateModelValues() throws Exception
         {
            hotelSearch = (HotelSearching) Component.getInstance("hotelSearch", true);
            hotelSearch.setSearchString("Union Square");
         }

         @Override
         protected void invokeApplication()
         {
            HotelBooking hotelBooking = (HotelBooking) Component.getInstance("hotelBooking", true);
            String outcome = hotelBooking.selectHotel(hotelSearch.getSelectedHotel());
            assert "hotel".equals( outcome );
         }

         @Override
         protected void renderResponse()
         {
            Hotel hotel = (Hotel) Contexts.getConversationContext().get("hotel");
            assert hotel.getCity().equals("NY");
            assert hotel.getZip().equals("10011");
            assert Manager.instance().isLongRunningConversation();
         }
         
      }.run();
      
      id = new Script(id) {

         @Override
         protected void applyRequestValues()
         {
            Contexts.getSessionContext().set("loggedIn", true);
            Contexts.getSessionContext().set("user", new User("Gavin King", "foobar", "gavin"));
         }

         @Override
         protected void invokeApplication()
         {
            HotelBooking hotelBooking = (HotelBooking) Component.getInstance("hotelBooking", true);
            String outcome = hotelBooking.bookHotel();
            assert "book".equals( outcome );
         }

         @Override
         protected void renderResponse()
         {
            Booking booking = (Booking) Contexts.getConversationContext().get("booking");
            assert booking.getUser()!=null;
            assert booking.getHotel()!=null;
            assert booking.getHotel()==Contexts.getConversationContext().get("hotel");
            assert booking.getUser()==Contexts.getSessionContext().get("user");
            assert Manager.instance().isLongRunningConversation();
            assert booking.getCreditCard()==null;
            assert booking.getCreditCardName()==null;
         }
         
      }.run();
      
      new Script(id) {

         @Override
         protected void processValidations() throws Exception
         {
            validate(Booking.class, "creditCard", "123");
            assert isValidationFailure();
         }

         @Override
         protected void renderResponse()
         {
            Iterator messages = FacesContext.getCurrentInstance().getMessages();
            assert messages.hasNext();
            assert ( (FacesMessage) messages.next() ).getSummary().equals("Credit card number must 16 digits long");
            assert !messages.hasNext();
            assert Manager.instance().isLongRunningConversation();
         }
         
      }.run();
      
      new Script(id) {

         @Override
         protected void processValidations() throws Exception
         {
            validate(Booking.class, "creditCardName", "");
            assert isValidationFailure();
         }

         @Override
         protected void renderResponse()
         {
            Iterator messages = FacesContext.getCurrentInstance().getMessages();
            assert messages.hasNext();
            assert ( (FacesMessage) messages.next() ).getSummary().equals("Credit card name is required");
            assert !messages.hasNext();
            assert Manager.instance().isLongRunningConversation();
         }
         
      }.run();
      
      new Script(id) {
         
         @Override @SuppressWarnings("deprecation")
         protected void updateModelValues() throws Exception
         {  
            Booking booking = (Booking) Contexts.getConversationContext().get("booking");
            booking.setCreditCard("1234567891021234");
            booking.setCreditCardName("GAVIN KING");
            booking.setBeds(2);
            Date now = new Date();
            booking.setCheckinDate(now);
            booking.setCheckoutDate(now);
         }

         @Override
         protected void invokeApplication()
         {
            HotelBooking hotelBooking = (HotelBooking) Contexts.getConversationContext().get("hotelBooking");
            String outcome = hotelBooking.setBookingDetails();
            assert outcome==null;
         }

         @Override
         protected void renderResponse()
         {
            Iterator messages = FacesContext.getCurrentInstance().getMessages();
            assert messages.hasNext();
            FacesMessage message = (FacesMessage) messages.next();
            assert message.getSummary().equals("Check out date must be later than check in date");
            assert !messages.hasNext();
            assert Manager.instance().isLongRunningConversation();
         }
         
      }.run();
      
      new Script(id) {
         
         @Override @SuppressWarnings("deprecation")
         protected void updateModelValues() throws Exception
         {
            Booking booking = (Booking) Contexts.getConversationContext().get("booking");
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, 2);
            booking.setCheckoutDate( cal.getTime() );
         }

         @Override
         protected void invokeApplication()
         {
            HotelBooking hotelBooking = (HotelBooking) Contexts.getConversationContext().get("hotelBooking");
            String outcome = hotelBooking.setBookingDetails();
            assert "confirm".equals(outcome);
         }

         @Override
         protected void renderResponse()
         {
            assert Manager.instance().isLongRunningConversation();
         }
         
      }.run();
      
      new Script(id) {
        
         @Override
         protected void applyRequestValues()
         {
            Contexts.getSessionContext().set("loggedIn", true);
            Contexts.getSessionContext().set("user", new User("Gavin King", "foobar", "gavin"));
         }

         @Override
         protected void invokeApplication()
         {
            HotelBooking hotelBooking = (HotelBooking) Contexts.getConversationContext().get("hotelBooking");
            String outcome = hotelBooking.confirm();
            assert "confirmed".equals( outcome );
         }

         @Override
         protected void renderResponse()
         {
            assert !Manager.instance().isLongRunningConversation();
         }
         
      }.run();
      
      new Script() {

         @Override
         protected void renderResponse()
         {
            ListDataModel bookings = (ListDataModel) Component.getInstance("bookings", true);
            assert bookings.getRowCount()==1;
            bookings.setRowIndex(0);
            Booking booking = (Booking) bookings.getRowData();
            assert booking.getHotel().getCity().equals("NY");
            assert booking.getUser().getUsername().equals("gavin");
            assert !Manager.instance().isLongRunningConversation();
         }
         
      }.run();
      
      new Script() {
         
         @Override
         protected void invokeApplication()
         {
            ListDataModel bookings = (ListDataModel) Component.getInstance("bookings", false);
            bookings.setRowIndex(0);
            BookingList bookingList = (BookingList) Component.getInstance("bookingList", true);
            String outcome = bookingList.cancel();
            assert "main".equals(outcome);
         }

         @Override
         protected void renderResponse()
         {
            ListDataModel bookings = (ListDataModel) Component.getInstance("bookings", true);
            assert bookings.getRowCount()==0;
            assert !Manager.instance().isLongRunningConversation();
         }
         
      }.run();
      
   }
   
}

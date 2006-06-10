//$Id$
package org.jboss.seam.example.hibernate.test;

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
import org.jboss.seam.example.hibernate.Booking;
import org.jboss.seam.example.hibernate.BookingListAction;
import org.jboss.seam.example.hibernate.Hotel;
import org.jboss.seam.example.hibernate.HotelBookingAction;
import org.jboss.seam.example.hibernate.User;
import org.jboss.seam.jsf.SeamExtendedManagedPersistencePhaseListener;
import org.jboss.seam.jsf.SeamPhaseListener;
import org.jboss.seam.mock.SeamTest;
import org.testng.annotations.Test;

public class BookingTest extends SeamTest
{
   
   @Test
   public void testBookHotel() throws Exception
   {

      String id = new Script() {

         HotelBookingAction hotelBooking;
         
         @Override
         protected void applyRequestValues()
         {
            Contexts.getSessionContext().set("loggedIn", true);
            Contexts.getSessionContext().set("user", new User("Gavin King", "foobar", "gavin"));
         }

         @Override
         protected void updateModelValues() throws Exception
         {
            hotelBooking = (HotelBookingAction) Component.getInstance("hotelBooking", true);
            hotelBooking.setSearchString("Union Square");
         }

         @Override
         protected void invokeApplication()
         {
            String outcome = hotelBooking.find();
            assert "main".equals( outcome );
         }

         @Override
         protected void renderResponse()
         {
            DataModel hotelsDataModel = (DataModel) Contexts.getConversationContext().get("hotels");
            assert hotelsDataModel.getRowCount()==1;
            assert ( (Hotel) hotelsDataModel.getRowData() ).getCity().equals("NY");
            assert "Union Square".equals( hotelBooking.getSearchString() );
            assert Manager.instance().isLongRunningConversation();
         }
         
      }.run();
      
      new Script(id) {

         @Override
         protected void invokeApplication()
         {
            //getRequest().getParameterMap().put("hotelId", "2");
        	   HotelBookingAction hotelBooking = (HotelBookingAction) Contexts.getConversationContext().get("hotelBooking");
            String outcome = hotelBooking.selectHotel();
            assert "selected".equals( outcome );
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
      
      new Script(id) {

         @Override
         protected void invokeApplication()
         {
        	   HotelBookingAction hotelBooking = (HotelBookingAction) Contexts.getConversationContext().get("hotelBooking");
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
         }
         
      }.run();
            
      new Script(id) {
         
         @Override @SuppressWarnings("deprecation")
         protected void updateModelValues() throws Exception
         {
            Booking booking = (Booking) Contexts.getConversationContext().get("booking");
            booking.setCreditCard("1234567891021234");
            Date now = new Date();
            booking.setCheckinDate(now);
            booking.setCheckoutDate(now);
         }

         @Override
         protected void invokeApplication()
         {
        	   HotelBookingAction hotelBooking = (HotelBookingAction) Contexts.getConversationContext().get("hotelBooking");
            String outcome = hotelBooking.setBookingDetails();
            assert outcome==null;
         }

         @Override
         protected void renderResponse()
         {
            Iterator messages = FacesContext.getCurrentInstance().getMessages();
            assert messages.hasNext();
            assert ( (FacesMessage) messages.next() ).getSummary().equals("Check out date must be later than check in date");
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
        	   HotelBookingAction hotelBooking = (HotelBookingAction) Contexts.getConversationContext().get("hotelBooking");
            String outcome = hotelBooking.setBookingDetails();
            assert "confirm".equals( outcome );
         }

         @Override
         protected void renderResponse()
         {
            assert Manager.instance().isLongRunningConversation();
         }
         
      }.run();
      
      new Script(id) {
        
         @Override
         protected void invokeApplication()
         {
        	   HotelBookingAction hotelBooking = (HotelBookingAction) Contexts.getConversationContext().get("hotelBooking");
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
            BookingListAction bookingList = (BookingListAction) Component.getInstance("bookingList", true);
            String outcome = bookingList.cancel();
            assert "cancelled".equals( outcome );
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
   
   @Override
   public SeamPhaseListener createPhaseListener()
   {
	   return new SeamExtendedManagedPersistencePhaseListener();
   }
   
}

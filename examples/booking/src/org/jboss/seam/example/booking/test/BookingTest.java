//$Id$
package org.jboss.seam.example.booking.test;

import java.util.Map;

import javax.faces.model.DataModel;

import org.jboss.seam.Component;
import org.jboss.seam.components.ConversationManager;
import org.jboss.seam.components.Settings;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.example.booking.Booking;
import org.jboss.seam.example.booking.Hotel;
import org.jboss.seam.example.booking.HotelBooking;
import org.jboss.seam.example.booking.HotelBookingAction;
import org.jboss.seam.example.booking.User;
import org.jboss.seam.mock.SeamTest;
import org.jboss.seam.util.Strings;
import org.testng.annotations.Test;

public class BookingTest extends SeamTest
{
   
   @Test
   public void testBookHotel() throws Exception
   {
      
      String id = new Script() {

         HotelBooking hotelBooking;
         
         @Override
         protected void applyRequestValues()
         {
            Contexts.getSessionContext().set("loggedIn", "true");
            Contexts.getSessionContext().set("user", new User("Gavin King", "foobar", "gavin"));
         }

         @Override
         protected void updateModelValues() throws Exception
         {
            hotelBooking = (HotelBooking) Component.getInstance("hotelBooking", true);
            hotelBooking.setSearchString("NY");
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
            DataModel dm = (DataModel) Contexts.getConversationContext().get("hotelsDataModel");
            assert dm.isRowAvailable();
            assert ( (Hotel) dm.getRowData() ).getCity().equals("NY");
            assert dm.getRowCount()==1;
            assert "NY".equals( hotelBooking.getSearchString() );
            assert ConversationManager.instance().isLongRunningConversation();
         }
         
      }.run();
      
      new Script(id) {

         @Override
         protected void invokeApplication()
         {
            HotelBooking hotelBooking = (HotelBooking) Contexts.getConversationContext().get("hotelBooking");
            String outcome = hotelBooking.selectHotel();
            assert "selected".equals( outcome );
         }

         @Override
         protected void renderResponse()
         {
            Hotel hotel = (Hotel) Contexts.getConversationContext().get("hotel");
            assert hotel.getCity().equals("NY");
            assert hotel.getZip().equals("11111");
            assert ConversationManager.instance().isLongRunningConversation();
         }
         
      }.run();
      
      new Script(id) {

         @Override
         protected void invokeApplication()
         {
            HotelBooking hotelBooking = (HotelBooking) Contexts.getConversationContext().get("hotelBooking");
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
            assert ConversationManager.instance().isLongRunningConversation();
         }
         
      }.run();
      
      new Script(id) {

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
            assert ConversationManager.instance().isLongRunningConversation();
         }
         
      }.run();
      
      new Script(id) {
         
         @Override @SuppressWarnings("deprecation")
         protected void updateModelValues() throws Exception
         {
            Booking booking = (Booking) Contexts.getConversationContext().get("booking");
            booking.setCreditCard("1234567891021234");
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
            assert ConversationManager.instance().isLongRunningConversation();
         }
         
      }.run();
      
      new Script(id) {
         
         @Override @SuppressWarnings("deprecation")
         protected void updateModelValues() throws Exception
         {
            Booking booking = (Booking) Contexts.getConversationContext().get("booking");
            booking.getCheckinDate().setDate(10); 
            booking.getCheckoutDate().setDate(12);
         }

         @Override
         protected void invokeApplication()
         {
            HotelBooking hotelBooking = (HotelBooking) Contexts.getConversationContext().get("hotelBooking");
            String outcome = hotelBooking.setBookingDetails();
            assert "success".equals( outcome );
         }

         @Override
         protected void renderResponse()
         {
            assert ConversationManager.instance().isLongRunningConversation();
         }
         
      }.run();
      
      new Script(id) {
        
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
            assert !ConversationManager.instance().isLongRunningConversation();
         }
         
      }.run();
      
   }

   @Override
   public void initServletContext(Map initParams)
   {
      initParams.put(Settings.PERSISTENCE_UNIT_NAMES, "bookingDatabase");
      String classNames = Strings.toString(HotelBookingAction.class, User.class, Booking.class, Hotel.class);
      initParams.put(Settings.COMPONENT_CLASS_NAMES, classNames);
   }
   
}

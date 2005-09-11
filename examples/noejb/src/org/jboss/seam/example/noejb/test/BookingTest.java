//$Id$
package org.jboss.seam.example.noejb.test;

import java.util.Iterator;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;

import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Hibernate;
import org.jboss.seam.core.Init;
import org.jboss.seam.core.Jndi;
import org.jboss.seam.core.Manager;
import org.jboss.seam.core.Jta;
import org.jboss.seam.example.noejb.Booking;
import org.jboss.seam.example.noejb.BookingListAction;
import org.jboss.seam.example.noejb.Hotel;
import org.jboss.seam.example.noejb.HotelBookingAction;
import org.jboss.seam.example.noejb.User;
import org.jboss.seam.jsf.SeamExtendedManagedPersistencePhaseListener;
import org.jboss.seam.jsf.SeamPhaseListener;
import org.jboss.seam.mock.SeamTest;
import org.jboss.seam.util.Strings;
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
            assert Manager.instance().isLongRunningConversation();
         }
         
      }.run();
      
      new Script(id) {

         @Override
         protected void invokeApplication()
         {
        	   HotelBookingAction hotelBooking = (HotelBookingAction) Contexts.getConversationContext().get("hotelBooking");
            String outcome = hotelBooking.selectHotel();
            assert "selected".equals( outcome );
         }

         @Override
         protected void renderResponse()
         {
            Hotel hotel = (Hotel) Contexts.getConversationContext().get("hotel");
            assert hotel.getCity().equals("NY");
            assert hotel.getZip().equals("11111");
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
            assert ( (FacesMessage) messages.next() ).getSummary().equals("Credit card number is required");
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
            assert ( (FacesMessage) messages.next() ).getSummary().equals("Check in date must be later than check out date");
            assert !messages.hasNext();
            assert Manager.instance().isLongRunningConversation();
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
        	 HotelBookingAction hotelBooking = (HotelBookingAction) Contexts.getConversationContext().get("hotelBooking");
            String outcome = hotelBooking.setBookingDetails();
            assert "success".equals( outcome );
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
         protected void invokeApplication()
         {
            BookingListAction bookingList = (BookingListAction) Component.getInstance("bookingList", true);
            String outcome = bookingList.find();
            assert "bookings".equals( outcome );
         }

         @Override
         protected void renderResponse()
         {
            DataModel dm = (DataModel) Contexts.getConversationContext().get("bookingsDataModel");
            assert dm.isRowAvailable();
            assert dm.getRowCount()==1;
            assert ( (Booking) dm.getRowData() ).getHotel().getCity().equals("NY");
            assert ( (Booking) dm.getRowData() ).getUser().getUsername().equals("gavin");
            assert !Manager.instance().isLongRunningConversation();
         }
         
      }.run();
      
   }

   @Override
   public void initServletContext(Map initParams)
   {
      initParams.put(Init.MANAGED_SESSIONS, "bookingDatabase");
      String classNames = Strings.toString(Jndi.class, Jta.class, Hibernate.class);
      initParams.put(Init.COMPONENT_CLASSES, classNames);
      initParams.put(Init.MANAGED_DATA_SOURCES, "bookingDatasource");
      initParams.put("bookingDatasource.driverClass", "org.hsqldb.jdbcDriver");
      initParams.put("bookingDatasource.connectionUrl", "jdbc:hsqldb:.");
      initParams.put("bookingDatasource.userName", "sa");
      initParams.put("bookingDatasource.jndiName", "java:bookingDatasource");
      initParams.put("org.jboss.seam.core.hibernate.dataSourceName", "bookingDatasource");
   }
   
   @Override
   public SeamPhaseListener createPhaseListener()
   {
	   return new SeamExtendedManagedPersistencePhaseListener();
   }
   
}

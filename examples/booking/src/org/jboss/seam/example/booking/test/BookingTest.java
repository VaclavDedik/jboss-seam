//$Id$
package org.jboss.seam.example.booking.test;

import java.util.Map;

import org.jboss.seam.Components;
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
   public void testStuff() throws Exception
   {
      
      new Script() {

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
            hotelBooking = (HotelBooking) Components.getComponentInstance("hotelBooking", true);
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
            assert "NY".equals( hotelBooking.getSearchString() );
            assert Contexts.isLongRunningConversation();
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

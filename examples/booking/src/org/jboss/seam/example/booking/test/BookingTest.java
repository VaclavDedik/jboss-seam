//$Id$
package org.jboss.seam.example.booking.test;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.example.booking.HotelBooking;
import org.jboss.seam.example.booking.User;
import org.jboss.seam.finders.ComponentFinder;
import org.jboss.seam.mock.SeamTest;
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
            hotelBooking = (HotelBooking) ComponentFinder.getComponentInstance("hotelBooking", true);
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
   
}

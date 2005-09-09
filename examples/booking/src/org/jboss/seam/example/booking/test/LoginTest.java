//$Id$
package org.jboss.seam.example.booking.test;

import java.util.Map;

import org.jboss.seam.Component;
import org.jboss.seam.Seam;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Ejb;
import org.jboss.seam.core.Manager;
import org.jboss.seam.core.Init;
import org.jboss.seam.example.booking.Booking;
import org.jboss.seam.example.booking.Hotel;
import org.jboss.seam.example.booking.HotelBooking;
import org.jboss.seam.example.booking.HotelBookingAction;
import org.jboss.seam.example.booking.Login;
import org.jboss.seam.example.booking.LoginAction;
import org.jboss.seam.example.booking.Logout;
import org.jboss.seam.example.booking.LogoutAction;
import org.jboss.seam.example.booking.User;
import org.jboss.seam.mock.SeamTest;
import org.jboss.seam.util.Strings;
import org.testng.annotations.Test;

public class LoginTest extends SeamTest
{
   
   @Test
   public void testLogin() throws Exception
   {
      
      new Script() {
         
         @Override
         protected void invokeApplication()
         {
            assert !isSessionInvalid();
            HotelBooking hb = (HotelBooking) Component.getInstance("hotelBooking", true);
            String outcome = hb.find();
            assert "login".equals( outcome );
         }

         @Override
         protected void renderResponse()
         {
            assert !Manager.instance().isLongRunningConversation();
            assert Contexts.getSessionContext().get("loggedIn")==null;

         }
         
      }.run();
      
      new Script() {

         @Override
         protected void updateModelValues() throws Exception
         {
            assert !isSessionInvalid();
            User user = (User) Component.getInstance("user", true);
            user.setUsername("gavin");
            user.setPassword("foobar");
         }

         @Override
         protected void invokeApplication()
         {
            Login login = (Login) Component.getInstance("login", true);
            String outcome = login.login();
            assert "main".equals( outcome );
         }

         @Override
         protected void renderResponse()
         {
            User user = (User) Component.getInstance("user", false);
            assert user.getName().equals("Gavin King");
            assert user.getUsername().equals("gavin");
            assert user.getPassword().equals("foobar");
            assert !Manager.instance().isLongRunningConversation();
            assert Contexts.getSessionContext().get("loggedIn").equals(true);

         }
         
      }.run();
      
      String id = new Script() {

         @Override
         protected void invokeApplication()
         {
            HotelBooking hb = (HotelBooking) Component.getInstance("hotelBooking", true);
            String outcome = hb.find();
            assert "main".equals( outcome );
         }

         @Override
         protected void renderResponse()
         {
            assert Manager.instance().isLongRunningConversation();
            assert Contexts.getSessionContext().get("loggedIn").equals(true);

         }
         
      }.run();
      
      new Script(id) {

         @Override
         protected void invokeApplication()
         {
            assert Manager.instance().isLongRunningConversation();
            Logout logout = (Logout) Component.getInstance("logout", true);
            String outcome = logout.logout();
            assert "login".equals( outcome );
            assert Seam.isSessionInvalid();
         }

         @Override
         protected void renderResponse()
         {
            assert Seam.isSessionInvalid();
         }
         
      }.run();
      
      assert isSessionInvalid();
      
   }

   @Override
   public void initServletContext(Map initParams)
   {
      initParams.put(Init.PERSISTENCE_UNIT_NAMES, "bookingDatabase");
      String classNames = Strings.toString(Ejb.class, LoginAction.class, LogoutAction.class, HotelBookingAction.class, User.class, Booking.class, Hotel.class);
      initParams.put(Init.COMPONENT_CLASS_NAMES, classNames);
   }
   
}

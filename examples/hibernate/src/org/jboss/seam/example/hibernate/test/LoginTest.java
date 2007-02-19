//$Id$
package org.jboss.seam.example.hibernate.test;
import org.jboss.seam.Component;
import org.jboss.seam.Seam;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Manager;
import org.jboss.seam.example.hibernate.HotelBookingAction;
import org.jboss.seam.example.hibernate.User;
import org.jboss.seam.jsf.SeamPhaseListener;
import org.jboss.seam.jsf.TransactionalSeamPhaseListener;
import org.jboss.seam.mock.SeamTest;
import org.jboss.seam.security.Identity;
import org.testng.annotations.Test;
public class LoginTest extends SeamTest
{
   
   @Test
   public void testLogin() throws Exception
   {
      
      new FacesRequest("/home.xhtml") {
         
         @Override
         protected void invokeApplication()
         {
            assert !isSessionInvalid();
            HotelBookingAction hb = (HotelBookingAction) Component.getInstance("hotelBooking", true);
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
     
      new FacesRequest("/home.xhtml") {
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
            Identity identity = (Identity) Component.getInstance("identity", true);
            identity.setUsername("gavin");
            identity.setPassword("foobar");
            String outcome = identity.login();
            assert "loggedIn".equals( outcome );
         }
         @Override
         protected void renderResponse()
         {
            User user = (User) Component.getInstance("user", false);
            assert user.getName().equals("Gavin King");
            assert user.getUsername().equals("gavin");
            assert user.getPassword().equals("foobar");
            assert !Manager.instance().isLongRunningConversation();
         }         
      }.run();
      
      String id = new FacesRequest("/home.xhtml") {
         @Override
         protected void invokeApplication()
         {
            HotelBookingAction hb = (HotelBookingAction) Component.getInstance("hotelBooking", true);
            String outcome = hb.find();
            assert "main".equals( outcome );
         }
         @Override
         protected void renderResponse()
         {
            assert Manager.instance().isLongRunningConversation();
         }
         
      }.run();
      
      new FacesRequest("/main.xhtml", id) {
         @Override
         protected void invokeApplication()
         {
            assert Manager.instance().isLongRunningConversation();
            Identity identity = (Identity) Component.getInstance("identity", true);
            identity.logout();
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
   public SeamPhaseListener createPhaseListener()
   {
	   return new TransactionalSeamPhaseListener();
   }
}

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
import org.jboss.seam.mock.SeamTest.FacesRequest;
import org.jboss.seam.security.Identity;
import org.testng.annotations.Test;
public class LoginTest extends SeamTest
{
   
   @Test
   public void testLogin() throws Exception
   {
      
      new FacesRequest() {
         
         @Override
         protected void invokeApplication()
         {
            assert !isSessionInvalid();
            assert getValue("#{identity.loggedIn}").equals(false);
         }
         
      }.run();
      
     
      new FacesRequest() {

         @Override
         protected void updateModelValues() throws Exception
         {
            assert !isSessionInvalid();
            setValue("#{identity.username}", "gavin");
            setValue("#{identity.password}", "foobar");
         }

         @Override
         protected void invokeApplication()
         {
            invokeMethod("#{identity.login}");
         }

         @Override
         protected void renderResponse()
         {
            assert getValue("#{user.name}").equals("Gavin King");
            assert getValue("#{user.username}").equals("gavin");
            assert getValue("#{user.password}").equals("foobar");
            assert !Manager.instance().isLongRunningConversation();
            assert getValue("#{identity.loggedIn}").equals(true);
         }
         
      }.run();
      
      new FacesRequest() {

         @Override
         protected void invokeApplication()
         {
            assert !isSessionInvalid();
            assert getValue("#{identity.loggedIn}").equals(true);
         }
         
      }.run();
      
      
      new FacesRequest() {

         @Override
         protected void invokeApplication()
         {
            assert !Manager.instance().isLongRunningConversation();
            assert !isSessionInvalid();
            invokeMethod("#{identity.logout}");
            assert Seam.isSessionInvalid();
         }

         @Override
         protected void renderResponse()
         {
            assert getValue("#{identity.loggedIn}").equals(false);
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

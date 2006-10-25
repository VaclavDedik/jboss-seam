//$Id$
package org.jboss.seam.example.booking.test;

import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Manager;
import org.jboss.seam.example.booking.ChangePassword;
import org.jboss.seam.example.booking.User;
import org.jboss.seam.mock.SeamTest;
import org.testng.annotations.Test;

public class ChangePasswordTest extends SeamTest
{
   
   @Test
   public void testChangePassword() throws Exception
   {
      
      new FacesRequest() {
         
         @Override
         protected void invokeApplication() throws Exception
         {
            Contexts.getSessionContext().set("loggedIn", true);
            Contexts.getSessionContext().set("user", new User("Gavin King", "foobar", "gavin"));
         }
         
      }.run();
      
      new FacesRequest() {
         
         @Override
         protected void processValidations() throws Exception
         {
            validate(User.class, "password", "xxx");
            assert isValidationFailure();
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
      
      new FacesRequest() {

         ChangePassword changePassword;

         @Override
         protected void updateModelValues() throws Exception
         {
            User user = (User) Component.getInstance("user", true);
            user.setPassword("xxxyyy");
            changePassword = (ChangePassword) Component.getInstance("changePassword", true);
            changePassword.setVerify("xxyyyx");
         }

         @Override
         protected void invokeApplication()
         {
            String outcome = changePassword.changePassword();
            assert outcome==null;
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
      
      new FacesRequest() {

         ChangePassword changePassword;

         @Override
         protected void updateModelValues() throws Exception
         {
            User user = (User) Component.getInstance("user", true);
            user.setPassword("xxxyyy");
            changePassword = (ChangePassword) Component.getInstance("changePassword", true);
            changePassword.setVerify("xxxyyy");
         }

         @Override
         protected void invokeApplication()
         {
            String outcome = changePassword.changePassword();
            assert outcome.equals("main");
         }

         @Override
         protected void renderResponse()
         {
            User user = (User) Component.getInstance("user", false);
            assert user.getName().equals("Gavin King");
            assert user.getUsername().equals("gavin");
            assert user.getPassword().equals("xxxyyy");
            assert !Manager.instance().isLongRunningConversation();
            assert Contexts.getSessionContext().get("loggedIn").equals(true);

         }
         
      }.run();
      
      new FacesRequest() {

         ChangePassword changePassword;

         @Override
         protected void updateModelValues() throws Exception
         {
            User user = (User) Component.getInstance("user", true);
            assert user.getPassword().equals("xxxyyy");
            user.setPassword("foobar");
            changePassword = (ChangePassword) Component.getInstance("changePassword", true);
            changePassword.setVerify("foobar");
         }

         @Override
         protected void invokeApplication()
         {
            String outcome = changePassword.changePassword();
            assert outcome.equals("main");
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
      
   }

}

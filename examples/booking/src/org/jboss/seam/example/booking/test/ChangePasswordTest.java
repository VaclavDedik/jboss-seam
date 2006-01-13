//$Id$
package org.jboss.seam.example.booking.test;

import java.util.Map;

import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Ejb;
import org.jboss.seam.core.Init;
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
      
      new Script() {

         ChangePassword changePassword;
         
         @Override
         protected void applyRequestValues() throws Exception
         {
            Contexts.getSessionContext().set("loggedIn", true);
            Contexts.getSessionContext().set("user", new User("Gavin King", "foobar", "gavin"));
         }

         @Override
         protected void updateModelValues() throws Exception
         {
            User user = (User) Component.getInstance("user", true);
            user.setPassword("xxx");
            changePassword = (ChangePassword) Component.getInstance("changePassword", true);
            changePassword.setVerify("xxx");
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
            assert user.getPassword().equals("xxx");
            assert !Manager.instance().isLongRunningConversation();
            assert Contexts.getSessionContext().get("loggedIn").equals(true);

         }
         
      }.run();
      
      new Script() {

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
      
      new Script() {

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
      
      new Script() {

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

   @Override
   public void initServletContext(Map initParams)
   {
      initParams.put(Init.MANAGED_PERSISTENCE_CONTEXTS, "bookingDatabase");
      initParams.put(Init.COMPONENT_CLASSES, Ejb.class.getName());
      initParams.put(Init.JNDI_PATTERN, "#{ejbName}/local");
   }
   
}

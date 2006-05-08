//$Id$
package org.jboss.seam.example.hibernate.test;

import java.util.Map;

import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Init;
import org.jboss.seam.core.Manager;
import org.jboss.seam.core.Microcontainer;
import org.jboss.seam.example.hibernate.ChangePasswordAction;
import org.jboss.seam.example.hibernate.User;
import org.jboss.seam.jsf.SeamExtendedManagedPersistencePhaseListener;
import org.jboss.seam.jsf.SeamPhaseListener;
import org.jboss.seam.mock.SeamTest;
import org.testng.annotations.Test;

public class ChangePasswordTest extends SeamTest
{
   
   @Test
   public void testChangePassword() throws Exception
   {
      
      new Script() {

         ChangePasswordAction changePassword;
         
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
            user.setPassword("xxxyyy");
            changePassword = (ChangePasswordAction) Component.getInstance("changePassword", true);
            changePassword.setVerify("xxxyyx");
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

    	  ChangePasswordAction changePassword;

         @Override
         protected void updateModelValues() throws Exception
         {
            User user = (User) Component.getInstance("user", true);
            user.setPassword("xxxyyy");
            changePassword = (ChangePasswordAction) Component.getInstance("changePassword", true);
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

    	  ChangePasswordAction changePassword;

         @Override
         protected void updateModelValues() throws Exception
         {
            User user = (User) Component.getInstance("user", true);
            user.setPassword("xxxyyy");
            changePassword = (ChangePasswordAction) Component.getInstance("changePassword", true);
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

    	  ChangePasswordAction changePassword;

         @Override
         protected void updateModelValues() throws Exception
         {
            User user = (User) Component.getInstance("user", true);
            assert user.getPassword().equals("xxxyyy");
            user.setPassword("foobar");
            changePassword = (ChangePasswordAction) Component.getInstance("changePassword", true);
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
      initParams.put(Init.MANAGED_SESSIONS, "bookingDatabase");
      initParams.put(Init.COMPONENT_CLASSES, Microcontainer.class.getName());
   }
   
   @Override
   public SeamPhaseListener createPhaseListener()
   {
	   return new SeamExtendedManagedPersistencePhaseListener();
   }
}

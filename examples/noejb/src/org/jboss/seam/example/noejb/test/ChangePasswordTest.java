//$Id$
package org.jboss.seam.example.noejb.test;

import java.util.Map;

import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Hibernate;
import org.jboss.seam.core.Init;
import org.jboss.seam.core.Jndi;
import org.jboss.seam.core.Manager;
import org.jboss.seam.core.Tm;
import org.jboss.seam.example.noejb.Booking;
import org.jboss.seam.example.noejb.ChangePasswordAction;
import org.jboss.seam.example.noejb.Hotel;
import org.jboss.seam.example.noejb.User;
import org.jboss.seam.jsf.SeamExtendedManagedPersistencePhaseListener;
import org.jboss.seam.jsf.SeamPhaseListener;
import org.jboss.seam.mock.SeamTest;
import org.jboss.seam.util.Strings;
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
            user.setPassword("xxx");
            changePassword = (ChangePasswordAction) Component.getInstance("changePassword", true);
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
      initParams.put(Init.SESSION_FACTORY_NAMES, "bookingDatabase");
      String classNames = Strings.toString(Jndi.class, Tm.class, Hibernate.class, ChangePasswordAction.class, User.class, Booking.class, Hotel.class);
      initParams.put(Init.COMPONENT_CLASS_NAMES, classNames);
      initParams.put(Init.DATA_SOURCE_NAMES, "bookingDatasource");
      initParams.put("bookingDatasource.driverClass", "org.hsqldb.jdbcDriver");
      initParams.put("bookingDatasource.connectionUrl", "jdbc:hsqldb:.");
      initParams.put("bookingDatasource.userName", "sa");
   }
   
   @Override
   public SeamPhaseListener createPhaseListener()
   {
	   return new SeamExtendedManagedPersistencePhaseListener();
   }
}

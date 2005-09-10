//$Id$
package org.jboss.seam.example.noejb.test;

import java.util.Map;

import org.jboss.seam.Component;
import org.jboss.seam.Seam;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Hibernate;
import org.jboss.seam.core.Init;
import org.jboss.seam.core.Jndi;
import org.jboss.seam.core.Manager;
import org.jboss.seam.core.Jta;
import org.jboss.seam.example.noejb.HotelBookingAction;
import org.jboss.seam.example.noejb.LoginAction;
import org.jboss.seam.example.noejb.LogoutAction;
import org.jboss.seam.example.noejb.User;
import org.jboss.seam.jsf.SeamExtendedManagedPersistencePhaseListener;
import org.jboss.seam.jsf.SeamPhaseListener;
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
            LoginAction login = (LoginAction) Component.getInstance("login", true);
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
            HotelBookingAction hb = (HotelBookingAction) Component.getInstance("hotelBooking", true);
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
            LogoutAction logout = (LogoutAction) Component.getInstance("logout", true);
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
      initParams.put(Init.SESSION_FACTORY_NAMES, "bookingDatabase");
      String classNames = Strings.toString(Jndi.class, Jta.class, Hibernate.class);
      initParams.put(Init.COMPONENT_CLASS_NAMES, classNames);
      initParams.put(Init.DATA_SOURCE_NAMES, "java:bookingDatasource");
      initParams.put("java:bookingDatasource.driverClass", "org.hsqldb.jdbcDriver");
      initParams.put("java:bookingDatasource.connectionUrl", "jdbc:hsqldb:.");
      initParams.put("java:bookingDatasource.userName", "sa");
   }
   
   @Override
   public SeamPhaseListener createPhaseListener()
   {
	   return new SeamExtendedManagedPersistencePhaseListener();
   }
}

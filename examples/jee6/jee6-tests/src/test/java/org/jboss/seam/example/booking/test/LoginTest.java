//$Id: LoginTest.java 5810 2007-07-16 06:46:47Z gavin $
package org.jboss.seam.example.booking.test;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.Component;
import org.jboss.seam.annotations.In;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.Manager;
import org.jboss.seam.example.booking.User;
import org.jboss.seam.security.Identity;
import org.jboss.seam.web.Session;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class LoginTest
{
   @Deployment(name="LoginTest")
   @OverProtocol("Servlet 3.0") 
   public static Archive<?> createDeployment()
   {
      return Deployments.bookingDeployment();
   }

   @Before
   public void before() {
      Lifecycle.beginCall();
   }

   @After
   public void after() {
      Lifecycle.endCall();
   }

   @Test
   public void testLoginComponent() throws Exception
   {
      Identity identity = Identity.instance();

      assertFalse(identity.isLoggedIn());
      identity.setUsername("gavin");
      identity.setPassword("foobar");
      identity.login();

      User user = (User)Component.getInstance("user");
      assertEquals("Gavin King", user.getName());
      assertEquals("gavin", user.getUsername());
      assertEquals("foobar", user.getPassword());
      assertTrue(identity.isLoggedIn());
      identity.logout();
      assertFalse(identity.isLoggedIn());
      identity.setUsername("gavin");
      identity.setPassword("tiger");
      identity.login();
      assertFalse(identity.isLoggedIn());
   }

   @Test
   public void testLogin() throws Exception
   {
      Identity identity = Identity.instance();

      assertFalse(identity.isLoggedIn());

      identity.setUsername("gavin");
      identity.setPassword("foobar");
      identity.login();

      User user = (User)Component.getInstance("user");
      assertEquals("Gavin King", user.getName());
      assertEquals("gavin", user.getUsername());
      assertEquals("foobar", user.getPassword());
      assertFalse(Manager.instance().isLongRunningConversation());
      assertTrue(identity.isLoggedIn());

      identity.logout();
      assertTrue(Session.instance().isInvalid());

      assertFalse(identity.isLoggedIn());
   }

}

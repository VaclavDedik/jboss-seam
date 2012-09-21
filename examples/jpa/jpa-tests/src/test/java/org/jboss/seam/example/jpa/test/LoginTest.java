//$Id: LoginTest.java 6505 2007-10-12 11:24:54Z pmuir $
package org.jboss.seam.example.jpa.test;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.seam.core.Manager;
import org.jboss.seam.mock.JUnitSeamTest;
import org.jboss.seam.web.Session;
import org.jboss.seam.mock.SeamTest;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class LoginTest extends JUnitSeamTest
{

   @Deployment(name="LoginTest")
   @OverProtocol("Servlet 3.0")
   public static Archive<?> createDeployment()
   {
      WebArchive er = Deployments.jpaDeployment();

      er.addClasses(LoginTest.class);

      return er;
   }


   @Test
   public void testLoginComponent() throws Exception
   {
      new ComponentTest() {

         @Override
         protected void testComponents() throws Exception
         {
            assert getValue("#{identity.loggedIn}").equals(false);
            setValue("#{identity.username}", "gavin");
            setValue("#{identity.password}", "foobar");
            invokeMethod("#{identity.login}");
            assert getValue("#{user.name}").equals("Gavin King");
            assert getValue("#{user.username}").equals("gavin");
            assert getValue("#{user.password}").equals("foobar");
            assert getValue("#{identity.loggedIn}").equals(true);
            invokeMethod("#{identity.logout}");
            assert getValue("#{identity.loggedIn}").equals(false);
            setValue("#{identity.username}", "gavin");
            setValue("#{identity.password}", "tiger");
            invokeMethod("#{identity.login}");
            assert getValue("#{identity.loggedIn}").equals(false);
         }
         
      }.run();
   }
   
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
            invokeAction("#{identity.login}");
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
            assert Session.instance().isInvalid();
         }

         @Override
         protected void renderResponse()
         {
            assert getValue("#{identity.loggedIn}").equals(false);
            assert Session.instance().isInvalid();
         }
         
      }.run();
      
   }

}

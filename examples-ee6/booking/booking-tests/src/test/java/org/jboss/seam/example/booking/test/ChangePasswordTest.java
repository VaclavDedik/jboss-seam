//$Id: ChangePasswordTest.java 5810 2007-07-16 06:46:47Z gavin $
package org.jboss.seam.example.booking.test;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.Asset; 
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Manager;
import org.jboss.seam.example.booking.User;
import org.jboss.seam.mock.JUnitSeamTest;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ChangePasswordTest extends JUnitSeamTest
{
   @Deployment(name="ChangePasswordTest")
   @OverProtocol("Servlet 3.0") 
   public static Archive<?> createDeployment()
   {
	   EnterpriseArchive er = Deployments.bookingDeployment();
	      WebArchive web = er.getAsType(WebArchive.class, "booking-web.war");
	      
	      web.addClasses(ChangePasswordTest.class);
	    		  
	      return er;
   }
   
   @Test
   public void testChangePassword() throws Exception
   {
      
      new FacesRequest() {
         
         @Override
         protected void invokeApplication() throws Exception
         {
            Contexts.getSessionContext().set("user", new User("Gavin King", "foobar", "gavin"));
            setValue("#{identity.username}", "gavin");
            setValue("#{identity.password}", "foobar");
            invokeMethod("#{identity.login}");
         }
         
      }.run();
      
      new FacesRequest() {
         
         @Override
         protected void processValidations() throws Exception
         {
            validateValue("#{user.password}", "xxx");
            assert isValidationFailure();
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
         protected void updateModelValues() throws Exception
         {
            setValue("#{user.password}", "xxxyyy");
            setValue("#{changePassword.verify}", "xxyyyx");
         }

         @Override
         protected void invokeApplication()
         {
            assert invokeAction("#{changePassword.changePassword}")==null;
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
         protected void updateModelValues() throws Exception
         {
            setValue("#{user.password}", "xxxyyy");
            setValue("#{changePassword.verify}", "xxxyyy");
         }

         @Override
         protected void invokeApplication()
         {
            invokeMethod("#{changePassword.changePassword}");
         }

         @Override
         protected void renderResponse()
         {
            assert getValue("#{user.name}").equals("Gavin King");
            assert getValue("#{user.username}").equals("gavin");
            assert getValue("#{user.password}").equals("xxxyyy");
            assert !Manager.instance().isLongRunningConversation();
            assert getValue("#{identity.loggedIn}").equals(true);

         }
         
      }.run();
      
      new FacesRequest() {

         @Override
         protected void updateModelValues() throws Exception
         {
            assert getValue("#{user.password}").equals("xxxyyy");
            setValue("#{user.password}", "foobar");
            setValue("#{changePassword.verify}", "foobar");
         }

         @Override
         protected void invokeApplication()
         {
            invokeMethod("#{changePassword.changePassword}");
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
      
   }

}

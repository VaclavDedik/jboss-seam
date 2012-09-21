//$Id: RegisterTest.java 10428 2009-04-15 21:54:38Z norman.richards@jboss.com $
package org.jboss.seam.example.registration.test;

import java.io.File;

import javax.faces.context.FacesContext;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.mock.JUnitSeamTest;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class RegisterTest extends JUnitSeamTest
{
   @Deployment(name="RegisterTest")
   @OverProtocol("Servlet 3.0") 
   public static Archive<?> createDeployment()
   {
     EnterpriseArchive er = ShrinkWrap.create(ZipImporter.class, "seam-registration.ear").importFrom(new File("../registration-ear/target/seam-registration.ear"))
               .as(EnterpriseArchive.class);
     WebArchive web = er.getAsType(WebArchive.class, "registration-web.war");
     web.addClasses(RegisterTest.class);

     // Install org.jboss.seam.mock.MockSeamListener
     web.delete("/WEB-INF/web.xml");
     web.addAsWebInfResource("web.xml");
     
     return er;
  }
   
   @Test
   public void testLogin() throws Exception
   {
            
      new FacesRequest("/register.xhtml") {

         @Override
         protected void processValidations() throws Exception
         {
            validateValue("#{user.username}", "1ovthafew");
            validateValue("#{user.name}", "Gavin King");
            validateValue("#{user.password}", "secret");
            assert !isValidationFailure();
         }
         
         @Override
         protected void updateModelValues() throws Exception
         {
            setValue("#{user.username}", "1ovthafew");
            setValue("#{user.name}", "Gavin King");
            setValue("#{user.password}", "secret");
         }

         @Override
         protected void invokeApplication()
         {
            assert invokeMethod("#{register.register}").equals("/registered.xhtml");
            setOutcome("/registered.xhtml");
         }
         
         @Override
         protected void afterRequest()
         {
            assert isInvokeApplicationComplete();
            assert !isRenderResponseBegun();
         }
         
      }.run();
      
      new NonFacesRequest("/registered.xhtml")
      {

         @Override
         protected void renderResponse()
         {
            assert getValue("#{user.username}").equals("1ovthafew");
            assert getValue("#{user.password}").equals("secret");
            assert getValue("#{user.name}").equals("Gavin King");
         }
         
      }.run();
      
      new FacesRequest("/register.xhtml") {

         @Override
         protected void processValidations() throws Exception
         {
            validateValue("#{user.username}", "1ovthafew");
            validateValue("#{user.name}", "Gavin A King");
            validateValue("#{user.password}", "password");
         }
         
         @Override
         protected void updateModelValues() throws Exception
         {
            setValue("#{user.username}", "1ovthafew");
            setValue("#{user.name}", "Gavin A King");
            setValue("#{user.password}", "password");
         }

         @Override
         protected void invokeApplication()
         {
            assert invokeMethod("#{register.register}")==null;
         }
         
         @Override
         protected void renderResponse() throws Exception
         {
            assert FacesContext.getCurrentInstance().getMessages().hasNext();
         }
         
         @Override
         protected void afterRequest()
         {
            assert isInvokeApplicationComplete();
            assert isRenderResponseComplete();
         }
         
      }.run();
      
   }
   
}

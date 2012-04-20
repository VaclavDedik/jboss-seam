package org.jboss.seam.example.guice.test;

import java.io.File;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.example.guice.JuiceBar;
import org.jboss.seam.example.guice.Juice;
import org.jboss.seam.example.guice.AppleJuice;
import org.jboss.seam.example.guice.Orange;
import org.jboss.seam.example.guice.OrangeJuice;
import org.jboss.seam.mock.JUnitSeamTest;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Injector;
import com.google.inject.Key;

/**
 * @author Pawel Wrzeszcz (pwrzeszcz [at] jboss . org)
 */
@RunWith(Arquillian.class)
public class GuiceTest extends JUnitSeamTest
{
   @Deployment(name="GuiceTest")
   @OverProtocol("Servlet 3.0")
   public static Archive<?> createDeployment()
   {
      EnterpriseArchive ear = ShrinkWrap.create(ZipImporter.class, "seam-guice.ear").importFrom(new File("../guice-ear/target/seam-guice.ear"))
            .as(EnterpriseArchive.class);

      // Install org.jboss.seam.mock.MockSeamListener
      WebArchive web = ear.getAsType(WebArchive.class, "guice-web.war");
      web.delete("/WEB-INF/web.xml");
      web.addAsWebInfResource("web.xml");

      web.addClasses(GuiceTest.class);
      
      JavaArchive ejb =  ear.getAsType(JavaArchive.class, "guice-ejb.jar");
      ejb.addClasses(JuiceTestBar.class);
      
      return ear;
   }

   @Test
   public void testGuiceInjector() throws Exception
   {
      new ComponentTest()
      {
         @Override
         protected void testComponents() throws Exception
         {
            Injector injector = (Injector) getInstance("guiceExampleInjector");

            Object juiceInstance = injector.getInstance(Juice.class);
            assert juiceInstance instanceof AppleJuice : juiceInstance;

            Object orangeInstance = injector.getInstance(Key.get(Juice.class, Orange.class));
            assert orangeInstance instanceof OrangeJuice : orangeInstance;
         }
      }.run();
   }

   @Test
   public void testGuiceIntegration() throws Exception
   {
      new ComponentTest()
      {
         @Override
         protected void testComponents() throws Exception
         {
            JuiceBar juiceBar = (JuiceBar) getInstance("juiceBar");

            Juice juiceOfTheDay = juiceBar.getJuiceOfTheDay();
            assert "Apple Juice".equals(juiceOfTheDay.getName()) : juiceOfTheDay.getName();
            Juice anotherJuice = juiceBar.getAnotherJuice();
            assert "Orange Juice".equals(anotherJuice.getName()) : anotherJuice.getName();

            Juice juiceOfTheDay2 = juiceBar.getJuiceOfTheDay();
            assert juiceOfTheDay != juiceOfTheDay2 : "A new instance should be created by Guice.";
            Juice anotherJuice2 = juiceBar.getAnotherJuice();
            assert anotherJuice == anotherJuice2 : "Different instances returned for the singleton object.";
         }
      }.run();
   }

   @Test
   public void testGuiceDisinjection() throws Exception
   {
      new ComponentTest()
      {
         @Override
         protected void testComponents() throws Exception
         {
            JuiceTestBar juiceBar = (JuiceTestBar) getInstance("juiceTestBar");
            juiceBar.getJuiceOfTheDay(); // Call a method to trigger Guice injection

            assert juiceBar.getJuiceOfTheDay() != null;
            assert juiceBar.getAnotherJuice() != null;

            // Reflection to obtain the field value would not work here due to the proxy
            assert juiceBar.getJuiceOfTheDayBypassInterceptors() == null
                        : "Value injected by Guice was not cleared after the call";
            assert juiceBar.getAnotherJuiceBypassInterceptors() == null
                        : "Value injected by Guice was not cleared after the call";         
         }
      }.run();
   }
}

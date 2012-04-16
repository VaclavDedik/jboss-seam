package org.jboss.seam.example.restbay.test;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.mock.EnhancedMockHttpServletResponse;
import org.jboss.seam.mock.JUnitSeamTest;
import org.jboss.seam.mock.ResourceRequestEnvironment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.jboss.seam.mock.ResourceRequestEnvironment.Method;
import static org.jboss.seam.mock.ResourceRequestEnvironment.ResourceRequest;

import java.util.HashMap;
import java.util.Map;

@RunWith(Arquillian.class)
public class AuctionServiceTest extends JUnitSeamTest
{
   @Deployment(name="AuctionServiceTest")
   @OverProtocol("Servlet 3.0")
   public static Archive<?> createDeployment()
   {
      EnterpriseArchive er = Deployments.restbayDeployment();
      WebArchive web = er.getAsType(WebArchive.class, "restbay-web.war");
      web.addClasses(AuctionServiceTest.class);
      return er;
   }

   ResourceRequestEnvironment requestEnv;

   @Before
   public void prepareEnv() throws Exception
   {
      requestEnv = new ResourceRequestEnvironment(this)
      {
         @Override
         public Map<String, Object> getDefaultHeaders()
         {
            return new HashMap<String, Object>()
            {{
                  put("Accept", "text/plain");
               }};
         }
      };
   }

   @Test
   public void testCategories() throws Exception
   {

      // Just verify we can do that, even if it doesn't make much sense
      new ResourceRequest(new ResourceRequestEnvironment(this), Method.GET, "/restv1/category").run();
      
      reset();

      new ResourceRequest(requestEnv, Method.GET, "/restv1/category")
      {

         @Override
         protected void onResponse(EnhancedMockHttpServletResponse response)
         {
            assert response.getStatus() == 200;
            String[] lines = response.getContentAsString().split("\n");
            assert lines[0].equals("1,Antiques");
            assert lines[1].equals("2,Art");
            assert lines[2].equals("3,Books");
         }

      }.run();
      
      reset();

      new ResourceRequest(requestEnv, Method.GET, "/restv1/category/1")
      {

         @Override
         protected void onResponse(EnhancedMockHttpServletResponse response)
         {
            assert response.getStatus() == 200;
            assert response.getContentAsString().equals("Antiques");
         }

      }.run();

   }

   @Test
   public void testAuctions() throws Exception
   {

      new ResourceRequest(requestEnv, Method.GET, "/restv1/auction")
      {

         @Override
         protected void onResponse(EnhancedMockHttpServletResponse response)
         {
            assert response.getStatus() == 200;
            // TODO: Assert content
         }

      }.run();
      
      reset();

      new ResourceRequest(requestEnv, Method.GET, "/restv1/auction/19264723")
      {

         @Override
         protected void onResponse(EnhancedMockHttpServletResponse response)
         {
            assert response.getStatus() == 200;
            assert response.getContentAsString().equals("Whistler's Mother, original painting by James McNeill Whistler");
         }

      }.run();

   }

}

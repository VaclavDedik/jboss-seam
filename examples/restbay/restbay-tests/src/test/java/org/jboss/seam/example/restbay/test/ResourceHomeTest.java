package org.jboss.seam.example.restbay.test;

import static org.junit.Assert.assertEquals;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.mock.EnhancedMockHttpServletRequest;
import org.jboss.seam.mock.EnhancedMockHttpServletResponse;
import org.jboss.seam.mock.JUnitSeamTest;

import static org.jboss.seam.mock.ResourceRequestEnvironment.Method;
import static org.jboss.seam.mock.ResourceRequestEnvironment.ResourceRequest;
import org.jboss.seam.mock.ResourceRequestEnvironment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * 
 * @author Jozef Hartinger
 * 
 */
@RunWith(Arquillian.class)
public class ResourceHomeTest extends JUnitSeamTest
{
   @Deployment(name="ResourceHomeTest")
   @OverProtocol("Servlet 3.0")
   public static Archive<?> createDeployment()
   {
      EnterpriseArchive er = Deployments.restbayDeployment();
      WebArchive web = er.getAsType(WebArchive.class, "restbay-web.war");
      web.addClasses(ResourceHomeTest.class);
      return er;
   }

   public String[] getQueryPaths()
   {
      return new String[]{ "/configuredCategory", "/extendedCategory" };
   }

   @Test
   public void testResourceHomeRead() throws Exception
   {
      for (String resourcePath : getQueryPaths())
      {
         final String expectedResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><category><categoryId>1</categoryId><name>Antiques</name></category>";
         final String path = "/restv1" + resourcePath + "/1";

         new ResourceRequest(new ResourceRequestEnvironment(this), Method.GET, path)
         {

            @Override
            protected void prepareRequest(EnhancedMockHttpServletRequest request)
            {
               super.prepareRequest(request);
               request.addHeader("Accept", "application/xml");
            }

            @Override
            protected void onResponse(EnhancedMockHttpServletResponse response)
            {
               assertEquals("Unexpected response.", expectedResponse, response.getContentAsString());
            }

         }.run();
      }
   }

   @Test
   public void testResourceHomeCreate() throws Exception
   {
      for (String resourcePath : getQueryPaths())
      {
         final String name = "Airplanes";
         final String body = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><category><name>" + name
               + "</name></category>";
         final String mediaType = "application/xml";
         final String path = "/restv1" + resourcePath;

         new ResourceRequest(new ResourceRequestEnvironment(this), Method.POST, path)
         {
            @Override
            protected void prepareRequest(EnhancedMockHttpServletRequest request)
            {
               super.prepareRequest(request);
               // TODO for some reason content type must be set using both these
               // methods
               request.addHeader("Content-Type", mediaType);
               request.setContentType(mediaType);
               request.setContent(body.getBytes());
            }

            @Override
            protected void onResponse(EnhancedMockHttpServletResponse response)
            {
               assertEquals("Unexpected response code.", 201, response.getStatus(), 201);
            }

         }.run();
      }
   }

   @Test
   public void testResourceHomeUpdate() throws Exception
   {
      for (String resourcePath : getQueryPaths())
      {
         final String body = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><category><categoryId>5</categoryId><name>"
               + resourcePath.hashCode() + "</name></category>";
         final String mediaType = "application/xml";
         final String path = "/restv1" + resourcePath + "/5";

         new ResourceRequest(new ResourceRequestEnvironment(this), Method.PUT, path)
         {

            @Override
            protected void prepareRequest(EnhancedMockHttpServletRequest request)
            {
               super.prepareRequest(request);
               request.setContentType(mediaType);
               request.addHeader("Content-Type", mediaType);
               request.setContent(body.getBytes());
            }

            @Override
            protected void onResponse(EnhancedMockHttpServletResponse response)
            {
               assertEquals("Unexpected response code.", 204, response.getStatus());
            }

         }.run();

         reset();

         new ResourceRequest(new ResourceRequestEnvironment(this), Method.GET, path)
         {

            @Override
            protected void prepareRequest(EnhancedMockHttpServletRequest request)
            {
               super.prepareRequest(request);
               request.addHeader("Accept", mediaType);
            }

            @Override
            protected void onResponse(EnhancedMockHttpServletResponse response)
            {
               assertEquals("Unexpected response code.", 200, response.getStatus());
               assertEquals("Unexpected response.", body, response.getContentAsString());
            }

         }.run();
      }
   }

   @Test
   public void testResourceHomeDelete() throws Exception
   {

      final String path = "/restv1/configuredCategory/15004";

      new ResourceRequest(new ResourceRequestEnvironment(this), Method.DELETE, path)
      {

         @Override
         protected void onResponse(EnhancedMockHttpServletResponse response)
         {
            assertEquals("Unexpected response code.", 204, response.getStatus());
         }

      }.run();
      
      reset();
      
      new ResourceRequest(new ResourceRequestEnvironment(this), Method.GET, path)
      {

         @Override
         protected void prepareRequest(EnhancedMockHttpServletRequest request)
         {
            super.prepareRequest(request);
            request.addHeader("Accept", "application/xml");
         }

         @Override
         protected void onResponse(EnhancedMockHttpServletResponse response)
         {
            assertEquals("Unexpected response code.", response.getStatus(), 404);
         }

      }.run();
   }
}

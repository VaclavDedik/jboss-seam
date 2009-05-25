package org.jboss.seam.example.restbay.test;

import static org.testng.Assert.assertEquals;

import org.jboss.seam.example.restbay.test.fwk.MockHttpServletRequest;
import org.jboss.seam.example.restbay.test.fwk.MockHttpServletResponse;
import org.jboss.seam.example.restbay.test.fwk.ResourceSeamTest;
import org.testng.annotations.Test;

/**
 * This class tests RESTEasy integration together with Seam Security.
 *
 * @author Jozef Hartinger
 *
 */
public class SecurityTest extends ResourceSeamTest
{
   @Test
   public void basicAuthTest() throws Exception
   {
      new ResourceRequest(Method.GET, "/restv1/secured/resource/admin")
      {
         @Override
         protected void prepareRequest(MockHttpServletRequest request)
         {
            super.prepareRequest(request);
            request.addHeader("Accept", "text/plain");
            request.addHeader("Authorization", "BASIC ZGVtbzpkZW1v"); // demo:demo
         }

         @Override
         protected void onResponse(MockHttpServletResponse response)
         {
            assertEquals(response.getStatus(), 200, "Unexpected response code.");
            assertEquals(response.getContentAsString(), "false", "Unexpected response.");
         }

      }.run();
   }
   
   @Test
   public void invalidCredentialsBasicAuthTest() throws Exception
   {
      new ResourceRequest(Method.GET, "/restv1/secured/resource")
      {
         @Override
         protected void prepareRequest(MockHttpServletRequest request)
         {
            super.prepareRequest(request);
            request.addHeader("Accept", "text/plain");
            request.addHeader("Authorization", "BASIC ZGVtbzpvbWVk"); // demo:omed
         }
         
         @Override
         protected void onResponse(MockHttpServletResponse response)
         {
            assertEquals(response.getStatus(), 403, "Unexpected response code.");
         }
         
      }.run();
   }
   
   @Test
   public void adminRoleTest() throws Exception
   {
      new ResourceRequest(Method.GET, "/restv1/secured/resource/admin")
      {
         @Override
         protected void prepareRequest(MockHttpServletRequest request)
         {
            super.prepareRequest(request);
            request.addHeader("Accept", "text/plain");
            request.addHeader("Authorization", "BASIC YWRtaW46YWRtaW4="); // admin:admin
         }
         
         @Override
         protected void onResponse(MockHttpServletResponse response)
         {
            assertEquals(response.getStatus(), 200, "Unexpected response code.");
            assertEquals(response.getContentAsString(), "true");
         }
         
      }.run();
   }
   
   @Test
   public void adminRoleTestWithRestriction() throws Exception
   {
      new ResourceRequest(Method.GET, "/restv1/secured/resource/restrictedAdmin")
      {
         @Override
         protected void prepareRequest(MockHttpServletRequest request)
         {
            super.prepareRequest(request);
            request.addHeader("Accept", "text/plain");
            request.addHeader("Authorization", "BASIC YWRtaW46YWRtaW4="); // admin:admin
         }
         
         @Override
         protected void onResponse(MockHttpServletResponse response)
         {
            assertEquals(response.getStatus(), 200, "Unexpected response code.");
            assertEquals(response.getContentAsString(), "true");
         }
         
      }.run();
   }
   
}

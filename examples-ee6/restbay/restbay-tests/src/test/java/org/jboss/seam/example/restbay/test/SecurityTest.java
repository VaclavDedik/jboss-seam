package org.jboss.seam.example.restbay.test;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.mock.EnhancedMockHttpServletRequest;
import org.jboss.seam.mock.EnhancedMockHttpServletResponse;
import org.jboss.seam.mock.JUnitSeamTest;
import org.jboss.seam.mock.ResourceRequestEnvironment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import static org.jboss.seam.mock.ResourceRequestEnvironment.Method;
import static org.jboss.seam.mock.ResourceRequestEnvironment.ResourceRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

/**
 * This class tests RESTEasy integration together with Seam Security.
 *
 * @author Jozef Hartinger
 */
@RunWith(Arquillian.class)
public class SecurityTest extends JUnitSeamTest
{
   @Deployment(name="SecurityTest")
   @OverProtocol("Servlet 3.0")
   public static Archive<?> createDeployment()
   {
      EnterpriseArchive er = Deployments.restbayDeployment();
      WebArchive web = er.getAsType(WebArchive.class, "restbay-web.war");
      web.addClasses(SecurityTest.class);
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
   public void basicAuthTest() throws Exception
   {
      new ResourceRequest(requestEnv, Method.GET, "/restv1/secured/admin")
      {
         @Override
         protected void prepareRequest(EnhancedMockHttpServletRequest request)
         {
            super.prepareRequest(request);
            request.addHeader("Accept", "text/plain");
            request.addHeader("Authorization", "Basic ZGVtbzpkZW1v"); // demo:demo
         }

         @Override
         protected void onResponse(EnhancedMockHttpServletResponse response)
         {
            assertEquals("Unexpected response code.", 200, response.getStatus(), 200);
            assertEquals("Unexpected response.", "false", response.getContentAsString());
         }

      }.run();
   }

   @Test
   public void invalidCredentialsBasicAuthTest() throws Exception
   {
      new ResourceRequest(requestEnv, Method.GET, "/restv1/secured")
      {
         @Override
         protected void prepareRequest(EnhancedMockHttpServletRequest request)
         {
            super.prepareRequest(request);
            request.addHeader("Accept", "text/plain");
            request.addHeader("Authorization", "Basic ZGVtbzpvbWVk"); // demo:omed
         }

         @Override
         protected void onResponse(EnhancedMockHttpServletResponse response)
         {
            assertEquals(
                  "Invalid authentication header value",
                  "Basic realm=\"Seam RestBay Application\"",
                  response.getHeader("WWW-Authenticate")
            );
            assertEquals("Unexpected response code.", 401, response.getStatus());
         }

      }.run();
   }

   @Test
   public void adminRoleTest() throws Exception
   {
      new ResourceRequest(requestEnv, Method.GET, "/restv1/secured/admin")
      {
         @Override
         protected void prepareRequest(EnhancedMockHttpServletRequest request)
         {
            super.prepareRequest(request);
            request.addHeader("Accept", "text/plain");
            request.addHeader("Authorization", "Basic YWRtaW46YWRtaW4="); // admin:admin
         }

         @Override
         protected void onResponse(EnhancedMockHttpServletResponse response)
         {
            assertEquals("Unexpected response code.", 200, response.getStatus());
            assertEquals("true", response.getContentAsString());
         }

      }.run();
   }

   @Test
   public void adminRoleTestWithRestriction() throws Exception
   {
      new ResourceRequest(requestEnv, Method.GET, "/restv1/secured/restrictedAdmin")
      {
         @Override
         protected void prepareRequest(EnhancedMockHttpServletRequest request)
         {
            super.prepareRequest(request);
            request.addHeader("Accept", "text/plain");
            request.addHeader("Authorization", "Basic YWRtaW46YWRtaW4="); // admin:admin
         }

         @Override
         protected void onResponse(EnhancedMockHttpServletResponse response)
         {
            assertEquals("Unexpected response code.", 200, response.getStatus());
            assertEquals("true", response.getContentAsString());
         }

      }.run();
   }

   @Test
   public void invalidAdminAuthorization() throws Exception
   {
      new ResourceRequest(requestEnv, Method.GET, "/restv1/secured/restrictedAdmin")
      {
         @Override
         protected void prepareRequest(EnhancedMockHttpServletRequest request)
         {
            super.prepareRequest(request);
            request.addHeader("Accept", "text/plain");
            request.addHeader("Authorization", "Basic ZGVtbzpkZW1v"); // demo:demo
         }

         @Override
         protected void onResponse(EnhancedMockHttpServletResponse response)
         {
            // See AuthorizationException mapping to 403 in pages.xml!
            assertEquals("Unexpected response code.", 403, response.getStatus());
            assert response.getStatusMessage().startsWith("Not authorized to access resource");
         }

      }.run();
   }
   
   @Test
   // JBPAPP-3713
   public void ejbLookup() throws Exception
   {
      new ResourceRequest(requestEnv, Method.GET, "/restv1/secured/ejbLookup")
      {
         @Override
         protected void prepareRequest(EnhancedMockHttpServletRequest request)
         {
            super.prepareRequest(request);
            request.addHeader("Accept", "text/plain");
            request.addHeader("Authorization", "Basic ZGVtbzpkZW1v"); // demo:demo
         }
         
         @Override
         protected void onResponse(EnhancedMockHttpServletResponse response)
         {
            assertEquals("Unexpected response code.", 200, response.getStatus(), 200);
            assert response.getContentAsString().equals("true");
         }
         
      }.run();
   }
   
    @Test
   // JBPAPP-3713
   public void synchronizationsLookup() throws Exception
   {
      new ResourceRequest(requestEnv, Method.GET, "/restv1/secured/synchronizationsLookup")
      {
         @Override
         protected void prepareRequest(EnhancedMockHttpServletRequest request)
         {
            super.prepareRequest(request);
            request.addHeader("Accept", "text/plain");
            request.addHeader("Authorization", "Basic ZGVtbzpkZW1v"); // demo:demo
         }
         
         @Override
         protected void onResponse(EnhancedMockHttpServletResponse response)
         {
            assertEquals("Unexpected response code.", 200, response.getStatus());
            assert response.getContentAsString().equals("true");
         }
         
      }.run();
   }
}

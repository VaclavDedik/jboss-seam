package org.jboss.seam.example.restbay.test;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.mock.EnhancedMockHttpServletResponse;
import org.jboss.seam.mock.EnhancedMockHttpServletRequest;
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
import static org.junit.Assert.*;

import javax.servlet.http.Cookie;
import javax.ws.rs.core.MediaType;
import java.util.Map;
import java.util.HashMap;

/**
 * <p>
 * This is the test matrix for resources:
 * </p>
 *
 * <pre>
 *                                    | EVENT | CONVERSATION | SESSION | APPLICATION | STATELESS
 * ---------------------------------------------------------------------------------------------
 * Plain JAX-RS Resource              |  OK   |      -       |    -    |      -      |    -
 * ---------------------------------------------------------------------------------------------
 * POJO Seam Component Resource       |  OK   |      ?       |    ?    |     OK      |   OK
 * ---------------------------------------------------------------------------------------------
 * POJO interface-annotated Component |  OK   |      ?       |    ?    |     OK      |   OK
 * ---------------------------------------------------------------------------------------------
 * EJB Plain SLSB Resource            |   -   |      -       |    -    |      -      |   OK
 * ---------------------------------------------------------------------------------------------
 * EJB SLSB Seam Component Resource   |   -   |      -       |    -    |      -      |   OK
 * ---------------------------------------------------------------------------------------------
 * EJB SFSB Seam Component Resource   |   ?   |      ?       |    ?    |      ?      |    -
 * ---------------------------------------------------------------------------------------------
 * </pre>
 *
 * <p>
 * Note that all EJB resources are always @Path annotated on their interface, not the implementation class.
 * </p>
 *
 * <p>
 * This is the test matrix for providers:
 * </p>
 *
 * <pre>
 *                                    | EVENT | CONVERSATION | SESSION | APPLICATION | STATELESS
 * ---------------------------------------------------------------------------------------------
 * Plain JAX-RS Provider              |   -   |      -       |    -    |      -      |   OK
 * ---------------------------------------------------------------------------------------------
 * RESTEasy StringConverter Provider  |   -   |      -       |    -    |      -      |   OK
 * ---------------------------------------------------------------------------------------------
 * RESTEasy StringConverter Component |   ?   |      -       |    -    |      ?      |    ?
 * ---------------------------------------------------------------------------------------------
 * POJO Seam Component Provider       |   ?   |      -       |    -    |     OK      |    ?
 * ---------------------------------------------------------------------------------------------
 * POJO interface-annotated Component |   ?   |      -       |    -    |     OK      |    ?
 * ---------------------------------------------------------------------------------------------
 * EJB Plain SLSB Provider            |   -   |      -       |    -    |      -      |    ?
 * ---------------------------------------------------------------------------------------------
 * EJB SLSB Seam Component Provider   |   -   |      -       |    -    |      -      |    ?
 * ---------------------------------------------------------------------------------------------
 * EJB SFSB Seam Component Resource   |   ?   |      -       |    -    |      ?      |    -
 * ---------------------------------------------------------------------------------------------
 * </pre>
 *
 */
@RunWith(Arquillian.class)
public class BasicServiceTest extends JUnitSeamTest
{
   @Deployment(name="BasicServiceTest")
   @OverProtocol("Servlet 3.0")
   public static Archive<?> createDeployment()
   {
      EnterpriseArchive er = Deployments.restbayDeployment();
      WebArchive web = er.getAsType(WebArchive.class, "restbay-web.war");
      web.addClasses(BasicServiceTest.class);
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

         @Override
         public String getServletPath()
         {
            return "/override/seam/resource/is/not/my/path/for/SeamResourceServlet";
         }

      };
   }

   public String[] getQueryPaths()
   {
      return new String[] {
            "/restv1/plainTest",

            "/restv1/eventComponentTest",
            "/restv1/applicationComponentTest",
            "/restv1/statelessComponentTest",

            "/restv1/interfaceEventComponentTest",
            "/restv1/interfaceApplicationComponentTest",
            "/restv1/interfaceStatelessComponentTest",

            "/restv1/statelessEjbTest",
            "/restv1/statelessEjbComponentTest"
      };
   }

   @Test
   public void testRootResource() throws Exception {
      new ResourceRequest(requestEnv, Method.GET, "/restv1/")
      {

         @Override
         protected void onResponse(EnhancedMockHttpServletResponse response)
         {
            assertEquals(200, response.getStatus());
            assertEquals("Root", response.getContentAsString());
         }

      }.run();
   }

   @Test
   public void testExeptionMapping() throws Exception
   {
      for (String resourcePath : getQueryPaths())
      {
         new ResourceRequest(requestEnv, Method.GET, resourcePath + "/trigger/unsupported")
         {

            @Override
            protected void onResponse(EnhancedMockHttpServletResponse response)
            {
               assert response.getStatus() == 501;
               assert response.getStatusMessage().equals("The request operation is not supported: foo");
            }

         }.run();
      }
   }

   @Test
   public void testEchos() throws Exception
   {
      for (String resourcePath : getQueryPaths())
      {
         new ResourceRequest(requestEnv, Method.GET, resourcePath + "/echouri")
         {

            @Override
            protected void onResponse(EnhancedMockHttpServletResponse response)
            {
               assert response.getStatus() == 200;
               assert response.getContentAsString().endsWith("/echouri");
            }

         }.run();

         reset();

         new ResourceRequest(requestEnv, Method.GET, resourcePath + "/echoquery")
         {

            @Override
            protected void prepareRequest(EnhancedMockHttpServletRequest request)
            {
               request.setQueryString("asdf=123");
               request.addQueryParameter("bar", "bbb");
               request.addQueryParameter("baz", "bzzz");
            }

            @Override
            protected void onResponse(EnhancedMockHttpServletResponse response)
            {
               assert response.getStatus() == 200;
               assert response.getContentAsString().equals("bbb");
            }

         }.run();

         reset();

         new ResourceRequest(requestEnv, Method.GET, resourcePath + "/echoheader")
         {

            @Override
            protected void prepareRequest(EnhancedMockHttpServletRequest request)
            {
               request.addHeader("bar", "baz");
            }

            @Override
            protected void onResponse(EnhancedMockHttpServletResponse response)
            {
               assert response.getStatus() == 200;
               assert response.getContentAsString().equals("baz");
            }

         }.run();

         reset();

         new ResourceRequest(requestEnv, Method.GET, resourcePath + "/echocookie")
         {

            @Override
            protected void prepareRequest(EnhancedMockHttpServletRequest request)
            {
               request.addCookie(new Cookie("bar", "baz"));
            }

            @Override
            protected void onResponse(EnhancedMockHttpServletResponse response)
            {
               assert response.getStatus() == 200;
               assert response.getContentAsString().equals("baz");
            }

         }.run();

         reset();

         new ResourceRequest(requestEnv, Method.GET, resourcePath + "/foo/bar/asdf")
         {

            @Override
            protected void onResponse(EnhancedMockHttpServletResponse response)
            {

               assert response.getStatus() == 200;
               assert response.getContentAsString().equals("bar: asdf");
            }

         }.run();

         reset();

         new ResourceRequest(requestEnv, Method.GET, resourcePath + "/echotwoparams/foo/bar")
         {

            @Override
            protected void onResponse(EnhancedMockHttpServletResponse response)
            {
               assert response.getStatus() == 200;
               assert response.getContentAsString().equals("foobar");
            }

         }.run();
      }

   }

   @Test
   public void testEncoding() throws Exception
   {
      for (String resourcePath : getQueryPaths())
      {
         new ResourceRequest(requestEnv, Method.GET, resourcePath + "/echoencoded/foo bar")
         {

            @Override
            protected void onResponse(EnhancedMockHttpServletResponse response)
            {
               assert response.getStatus() == 200;
               assert response.getContentAsString().equals("foo%20bar");
            }

         }.run();
      }
   }

   @Test
   public void testFormHandling() throws Exception
   {
      for (String resourcePath : getQueryPaths())
      {
         new ResourceRequest(requestEnv, Method.POST, resourcePath + "/echoformparams")
         {

            @Override
            protected void prepareRequest(EnhancedMockHttpServletRequest request)
            {
               request.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
               request.addParameter("foo", new String[]
               {"bar", "baz"});
            }

            @Override
            protected void onResponse(EnhancedMockHttpServletResponse response)
            {
               assert response.getStatus() == 200;
               assert response.getContentAsString().equals("barbaz");
            }

         }.run();

         reset();

         new ResourceRequest(requestEnv, Method.POST, resourcePath + "/echoformparams2")
         {

            @Override
            protected void prepareRequest(EnhancedMockHttpServletRequest request)
            {
               request.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
               request.addParameter("foo", new String[]
               {"bar", "baz"});
            }

            @Override
            protected void onResponse(EnhancedMockHttpServletResponse response)
            {
               assert response.getStatus() == 200;
               assert response.getContentAsString().equals("barbaz");
            }

         }.run();

         reset();

         new ResourceRequest(requestEnv, Method.POST, resourcePath + "/echoformparams3")
         {

            @Override
            protected void prepareRequest(EnhancedMockHttpServletRequest request)
            {
               request.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
               request.addHeader("bar", "foo");
               request.addParameter("foo", new String[]
               {"bar", "baz"});
            }

            @Override
            protected void onResponse(EnhancedMockHttpServletResponse response)
            {
               assert response.getStatus() == 200;
               assert response.getContentAsString().equals("foobarbaz");
            }

         }.run();

         reset();
      }
   }

   @Test()
   public void testStringConverter() throws Exception
   {
      final String ISO_DATE = "2007-07-10T14:54:56-0500";
      final String ISO_DATE_MILLIS = "1184097296000";
      
      for (String resourcePath : getQueryPaths())
      {

         new ResourceRequest(requestEnv, Method.GET, resourcePath + "/convertDate/" + ISO_DATE)
         {

            @Override
            protected void onResponse(EnhancedMockHttpServletResponse response)
            {
               assert response.getStatus() == 200;
               assertEquals(ISO_DATE_MILLIS, response.getContentAsString());
            }

         }.run();
      }
   }

   @Test
   public void testProvider() throws Exception
   {
      for (String resourcePath : getQueryPaths())
      {
         new ResourceRequest(requestEnv, Method.GET, resourcePath + "/commaSeparated")
         {

            @Override
            protected void prepareRequest(EnhancedMockHttpServletRequest request)
            {
               request.addHeader("Accept", "text/csv");
            }

            @Override
            protected void onResponse(EnhancedMockHttpServletResponse response)
            {
               assert response.getStatus() == 200;
               assert response.getContentAsString().equals("foo,bar\r\nasdf,123\r\n");
            }

         }.run();

         reset();

         new ResourceRequest(requestEnv, Method.GET, resourcePath + "/commaSeparatedStrings")
         {

            @Override
            protected void prepareRequest(EnhancedMockHttpServletRequest request)
            {
               request.addHeader("Accept", "text/plain");
            }

            @Override
            protected void onResponse(EnhancedMockHttpServletResponse response)
            {
               assert response.getStatus() == 200;
               assert response.getContentAsString().equals("abc,foo,bar,baz");
            }

         }.run();

         reset();

         new ResourceRequest(requestEnv, Method.GET, resourcePath + "/commaSeparatedIntegers")
         {

            @Override
            protected void prepareRequest(EnhancedMockHttpServletRequest request)
            {
               request.addHeader("Accept", "text/plain");
            }

            @Override
            protected void onResponse(EnhancedMockHttpServletResponse response)
            {
               assert response.getStatus() == 200;
               assert response.getContentAsString().equals("abc,1,2,3");
            }
         };
         // }.run();
         // TODO: Retracted support for Seam component providers, injection shouldn't happen, see https://jira.jboss.org/jira/browse/JBSEAM-4247
      }
   }

   @Test
   // JBPAPP-3713
   public void synchronizationsLookup() throws Exception
   {
      new ResourceRequest(requestEnv, Method.GET, "/restv1/eventComponentTest/synchronizationsLookup")
      {
         @Override
         protected void prepareRequest(EnhancedMockHttpServletRequest request)
         {
            super.prepareRequest(request);
         }

         @Override
         protected void onResponse(EnhancedMockHttpServletResponse response)
         {
            assertEquals("Unexpected response code.", 200, response.getStatus(), 200);
            assert response.getContentAsString().equals("true");
         }

      }.run();
   }
}

package org.jboss.seam.example.restbay.test;

import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;

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
public class ResourceQueryTest extends JUnitSeamTest
{
   @Deployment(name="ResourceQueryTest")
   @OverProtocol("Servlet 3.0")
   public static Archive<?> createDeployment()
   {
      EnterpriseArchive er = Deployments.restbayDeployment();
      WebArchive web = er.getAsType(WebArchive.class, "restbay-web.war");
      web.addClasses(ResourceQueryTest.class);
      return er;
   }

   public String[] getQueryPaths()
   {
      String[] data = new String[2];
      data[0] = "/configuredCategory";
      data[1] = "/extendedCategory";
      return data;
   }

   @Test
   public void testResourceQuery() throws Exception
   {
      for (String path : getQueryPaths())
      {
         new ResourceRequest(new ResourceRequestEnvironment(this), Method.GET, "/restv1" + path)
         {

            @Override
            protected void prepareRequest(EnhancedMockHttpServletRequest request)
            {
               super.prepareRequest(request);
               request.addHeader("Accept", "application/xml");
               request.setQueryString("start=2&show=2");
            }

            @Override
            protected void onResponse(EnhancedMockHttpServletResponse response)
            {
               String responseString = response.getContentAsString();
               String expectedResponseRegex = "<\\?xml version=\"1\\.0\" encoding=\"UTF-8\" standalone=\"yes\"\\?>" +
               		"<collection>" +
               		   "(<category>" +
               		      "<categoryId>[^<]+</categoryId><name>[^<]+</name>" +
               		      "(<parent><categoryId>[^<]+</categoryId><name>[^<]+</name></parent>)?" +
               		   "</category>){2}" +
               		"</collection>";
               assertTrue("The response string doesn't match the expected response. " + responseString, Pattern.matches(expectedResponseRegex, responseString));
            }

         }.run();
      }
   }

}

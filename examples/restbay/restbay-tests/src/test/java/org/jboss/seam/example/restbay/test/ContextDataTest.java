package org.jboss.seam.example.restbay.test;

import static org.junit.Assert.assertEquals;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.mock.EnhancedMockHttpServletRequest;
import org.jboss.seam.mock.EnhancedMockHttpServletResponse;
import org.jboss.seam.mock.JUnitSeamTest;
import org.jboss.seam.mock.ResourceRequestEnvironment;
import org.jboss.seam.mock.ResourceRequestEnvironment.Method;
import org.jboss.seam.mock.ResourceRequestEnvironment.ResourceRequest;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Jozef Hartinger
 */
@RunWith(Arquillian.class)
public class ContextDataTest extends JUnitSeamTest
{
   @Deployment(name="ContextDataTest")
   @OverProtocol("Servlet 3.0")
   public static Archive<?> createDeployment()
   {
      EnterpriseArchive er = Deployments.restbayDeployment();
      WebArchive web = er.getAsType(WebArchive.class, "restbay-web.war");
      web.addClasses(ContextDataTest.class);
      return er;
   }
   
   public String[] getContextDataTypePaths()
   {
      return new String[]{ "/providers", "/registry", "/dispatcher" };
   }
   
   @Test
   public void testContextData() throws Exception
   {

      for (String pathSegment : getContextDataTypePaths())
      {
         String path = "/restv1/contextData" + pathSegment;

         new ResourceRequest(new ResourceRequestEnvironment(this), Method.GET, path)
         {

            @Override
            protected void prepareRequest(EnhancedMockHttpServletRequest request)
            {
               super.prepareRequest(request);
               request.addHeader("Accept", "text/plain");
            }

            @Override
            protected void onResponse(EnhancedMockHttpServletResponse response)
            {
               assertEquals("Unexpected response code.", 200, response.getStatus());
               assertEquals("Unexpected response.", "true", response.getContentAsString());
            }

         }.run();
      }
   }
}

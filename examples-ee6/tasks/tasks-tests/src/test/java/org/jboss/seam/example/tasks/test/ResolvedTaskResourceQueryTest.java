/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.seam.example.tasks.test;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;

import static org.jboss.seam.mock.ResourceRequestEnvironment.Method;
import static org.jboss.seam.mock.ResourceRequestEnvironment.ResourceRequest;
import org.jboss.seam.mock.EnhancedMockHttpServletRequest;
import org.jboss.seam.mock.EnhancedMockHttpServletResponse;
import org.jboss.seam.mock.JUnitSeamTest;
import org.jboss.seam.mock.ResourceRequestEnvironment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.runner.RunWith;

/**
 * Test class for /user/{username}/tasks/resolved part of API.
 * @author Jozef Hartinger
 *
 */
@RunWith(Arquillian.class)
public class ResolvedTaskResourceQueryTest extends JUnitSeamTest
{

   @Deployment(name="ResolvedTaskResourceQueryTest")
   @OverProtocol("Servlet 3.0")
   public static Archive<?> createDeployment()
   {
      EnterpriseArchive er = Deployments.tasksDeployment();
      WebArchive web = er.getAsType(WebArchive.class, "tasks-web.war");
      web.addClasses(ResolvedTaskResourceQueryTest.class);
      return er;
   }
    
   // We could do this BeforeClass only once but we can't do ResourceRequests there
   @Before
   public void resolveTask() throws Exception {
      
      final String mimeType = "application/xml";
      final String representation = "<task><id>14</id></task>";
      
      new ResourceRequest(new ResourceRequestEnvironment(this), Method.PUT, "/v1/auth/category/School/resolved/14")
      {

         @Override
         protected void prepareRequest(EnhancedMockHttpServletRequest request)
         {
            super.prepareRequest(request);
            request.addHeader("Authorization", "Basic ZGVtbzpkZW1v"); // demo:demo
            request.addHeader("Content-Type", mimeType);
            request.setContentType(mimeType);
            request.setContent(representation.getBytes());
         }

         @Override
         protected void onResponse(EnhancedMockHttpServletResponse response)
         {
            super.onResponse(response);
            assertEquals("Unexpected response code.", 204, response.getStatus());
         }

      }.run();
   }
   
   @Test
   public void editTaskTest() throws Exception
   {
      String[][] data = new String[][] {
            new String[] {"application/xml", "<name>Get a haircut</name>"},
            new String[] {"application/json", "\"name\":\"Get a haircut\""},
            new String[] {"application/atom+xml", "<atom:title>Get a haircut</atom:title>"}
      }; 
      
      for(final String[] caseData : data) {
        new ResourceRequest(new ResourceRequestEnvironment(this), Method.GET, "/v1/user/demo/tasks/resolved")
        {

            @Override
            protected void prepareRequest(EnhancedMockHttpServletRequest request)
            {
                super.prepareRequest(request);
                request.addHeader("Accept", caseData[0]);
            }

            @Override
            protected void onResponse(EnhancedMockHttpServletResponse response)
            {
                super.onResponse(response);
                assertEquals("Unexpected response code.", 200, response.getStatus());
                assertTrue("Unexpected response.", response.getContentAsString().contains(caseData[1]));
            }

        }.run();
      }
   }
}

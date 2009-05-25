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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.jboss.seam.example.tasks.test.fwk.MockHttpServletRequest;
import org.jboss.seam.example.tasks.test.fwk.MockHttpServletResponse;
import org.jboss.seam.example.tasks.test.fwk.ResourceSeamTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test class for /context part of API
 * @author Jozef Hartinger
 *
 */
public class ContextResourceTest extends ResourceSeamTest
{
   @DataProvider(name = "query")
   public String[][] getQueryData()
   {
      return new String[][] { new String[] { "application/xml", "<context><name>School</name></context>" }, new String[] { "application/json", "{\"context\":{\"name\":\"School\"}}" } };
   }

   @Test(dataProvider = "query")
   public void getContextListTest(final String contentType, final String expectedResponse) throws Exception
   {
      new ResourceRequest(Method.GET, "/v1/auth/context")
      {

         @Override
         protected void prepareRequest(MockHttpServletRequest request)
         {
            super.prepareRequest(request);
            request.addHeader("Accept", contentType);
            request.addHeader("Authorization", "Basic ZGVtbzpkZW1v"); // demo:demo
         }

         @Override
         protected void onResponse(MockHttpServletResponse response)
         {
            super.onResponse(response);
            assertEquals(response.getStatus(), 200, "Unexpected response code.");
            assertTrue(response.getContentAsString().contains(expectedResponse), "Unexpected response.");
         }

      }.run();
   }

   @Test
   public void deleteContextTest() throws Exception
   {
      new ResourceRequest(Method.DELETE, "/v1/auth/context/Work")
      {

         @Override
         protected void prepareRequest(MockHttpServletRequest request)
         {
            super.prepareRequest(request);
            request.addHeader("Accept", "application/xml");
            request.addHeader("Authorization", "Basic ZGVtbzpkZW1v"); // demo:demo
         }

         @Override
         protected void onResponse(MockHttpServletResponse response)
         {
            super.onResponse(response);
            assertEquals(response.getStatus(), 204, "Unexpected response code.");
         }

      }.run();
   }

   @Test
   public void createContextTest() throws Exception
   {
      
      final String uri = "/v1/auth/context/Test%20Context";
      final String mimeType = "application/json";
      final String expectedResponse = "{\"context\":{\"name\":\"Test Context\"}}";
      
      new ResourceRequest(Method.PUT, uri)
      {

         @Override
         protected void prepareRequest(MockHttpServletRequest request)
         {
            super.prepareRequest(request);
            request.addHeader("Authorization", "Basic ZGVtbzpkZW1v"); // demo:demo
         }

         @Override
         protected void onResponse(MockHttpServletResponse response)
         {
            super.onResponse(response);
            assertEquals(response.getStatus(), 201, "Unexpected response code.");
         }

      }.run();

      new ResourceRequest(Method.GET, uri)
      {

         @Override
         protected void prepareRequest(MockHttpServletRequest request)
         {
            super.prepareRequest(request);
            request.addHeader("Accept", mimeType);
            request.addHeader("Authorization", "Basic ZGVtbzpkZW1v"); // demo:demo
         }

         @Override
         protected void onResponse(MockHttpServletResponse response)
         {
            super.onResponse(response);
            assertEquals(response.getStatus(), 200, "Unexpected response code.");
            assertEquals(response.getContentAsString(), expectedResponse, "Unexpected response.");
         }

      }.run();
   }

   // @Test
   // TODO uncomment once JBSEAM-4152 is resolved
   public void noAuthorizationHeaderTest() throws Exception
   {
      new ResourceRequest(Method.GET, "/v1/auth/context")
      {

         @Override
         protected void prepareRequest(MockHttpServletRequest request)
         {
            super.prepareRequest(request);
            request.addHeader("Accept", "application/xml");
         }

         @Override
         protected void onResponse(MockHttpServletResponse response)
         {
            super.onResponse(response);
            assertEquals(response.getStatus(), 401, "Unexpected response code.");
         }

      }.run();
   }

}

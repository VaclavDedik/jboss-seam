/*
 *  * JBoss, Home of Professional Open Source  *  * Distributable under LGPL
 * license.  * See terms of license at gnu.org.  
 */
package org.jboss.seam.mock;

import org.testng.annotations.Configuration;

/**
 * Provides BaseSeamTest functionality for TestNG integration tests.
 * 
 * @author Gavin King
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @author Mike Youngstrom
 * @version $Revision$
 */
public class SeamTest extends BaseSeamTest
{

   @Configuration(beforeTestMethod = true)
   @Override
   public void begin()
   {
      super.begin();
   }

   @Configuration(afterTestMethod = true)
   @Override
   public void end()
   {
      super.end();
   }

   @Configuration(beforeTestClass = true)
   @Override
   public void init() throws Exception
   {
      super.init();
   }

   @Configuration(afterTestClass = true)
   @Override
   public void cleanup() throws Exception
   {
      super.cleanup();
   }

   /**
    * A pass through to BaseSeamTest.FacesRequest.
    * Perhaps these should be deprecated?
    */
   public class FacesRequest extends BaseSeamTest.FacesRequest
   {

      public FacesRequest()
      {
         super();
      }

      public FacesRequest(String viewId, String conversationId)
      {
         super(viewId, conversationId);
      }

      public FacesRequest(String viewId)
      {
         super(viewId);
      }

   }

   /**
    * A pass through to BaseSeamTest.NonFacesRequest.
    * Perhaps these should be deprecated?
    */
   public class NonFacesRequest extends BaseSeamTest.NonFacesRequest
   {

      public NonFacesRequest()
      {
         super();
      }

      public NonFacesRequest(String viewId, String conversationId)
      {
         super(viewId, conversationId);
      }

      public NonFacesRequest(String viewId)
      {
         super(viewId);
      }

   }

   /**
    * A pass through to BaseSeamTest.Request.
    * Perhaps these should be deprecated?
    */
   public abstract class Request extends BaseSeamTest.Request
   {

      public Request()
      {
         super();
      }

      public Request(String conversationId)
      {
         super(conversationId);
      }

   }

   /**
    * @deprecated Use FacesRequest or NonFacesRequest
    *             instead
    */
   @Deprecated
   public abstract class Script extends BaseSeamTest.Script
   {

      public Script()
      {
         super();
      }

      public Script(String conversationId)
      {
         super(conversationId);
      }
   }
}

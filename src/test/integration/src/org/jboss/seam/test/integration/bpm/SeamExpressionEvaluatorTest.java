package org.jboss.seam.test.integration.bpm;

import org.jboss.seam.mock.SeamTest;
import org.testng.annotations.Test;

/**
 * @author Pete Muir
 *
 */
public class SeamExpressionEvaluatorTest extends SeamTest
{

   // Test for JBSEAM-1937
   @Test
   public void testEvaluate() throws Exception
   {
      String cid = new FacesRequest()
      {

         @Override
         protected void invokeApplication() throws Exception
         {
            invokeAction("#{seamExpressionEvaluatorTestController.createProcess}");
         }
          
      }.run();
   }
   
}

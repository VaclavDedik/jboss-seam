package org.jboss.seam.test.integration;

import org.jboss.seam.RequiredException;
import org.jboss.seam.mock.SeamTest;
import org.testng.annotations.Test;

/**
 * @author Pete Muir
 *
 */
public class PageParamTest extends SeamTest
{

   @Test
   public void testPageParameter() throws Exception
   {
      new FacesRequest("/pageWithParameter.xhtml")
      {
         @Override
         protected void beforeRequest()
         {
            setParameter("personName", "pete");
         }
         
         @Override
         protected void invokeApplication() throws Exception
         {
            assert "pete".equals(getValue("#{person.name}"));
         }
      }.run();
      
      new FacesRequest("/pageWithParameter.xhtml")
      {
         @Override
         protected void beforeRequest()
         {
            setParameter("anotherPersonName", "pete");
         }
         
         @Override
         protected void invokeApplication() throws Exception
         {
            assert getValue("#{person.name}") == null;
         }
      }.run();
   }
   
   @Test
   public void testRequiredPageParameter() throws Exception
   {
      new FacesRequest("/pageWithRequiredParameter.xhtml")
      {
         @Override
         protected void beforeRequest()
         {
            setParameter("personName", "pete");
         }
         
         @Override
         protected void invokeApplication() throws Exception
         {
            assert "pete".equals(getValue("#{person.name}"));
         }
      }.run();
      
   }
   
}

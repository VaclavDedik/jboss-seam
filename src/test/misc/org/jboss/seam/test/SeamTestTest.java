package org.jboss.seam.test;

import org.jboss.seam.mock.SeamTest;
import org.testng.annotations.Test;

public class SeamTestTest extends SeamTest
{
   
   private static final String PETER_NAME = "Pete Muir";
   private static final String PETER_USERNAME = "pmuir";
   
   @Test
   public void testEl() throws Exception
   {
      new FacesRequest() 
      {  
         
         @Override
         protected void updateModelValues() throws Exception
         {
            setValue("#{user.name}", PETER_NAME);
         }
         
         @Override
         protected void renderResponse() throws Exception
         {
            assert getValue("#{user.name}").equals(PETER_NAME);
         }
      }.run();
   }
   
   @Test
   public void testSeamSecurity() throws Exception
   {
      new FacesRequest() 
      {  
         
         @Override
         protected void updateModelValues() throws Exception
         {
            setValue("#{identity.username}", PETER_USERNAME);
         }
         
         @Override
         protected void renderResponse() throws Exception
         {
            assert getValue("#{identity.username}").equals(PETER_USERNAME);
         }
      }.run();
   }

}

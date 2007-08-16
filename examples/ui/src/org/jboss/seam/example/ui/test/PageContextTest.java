package org.jboss.seam.example.ui.test;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.mock.SeamTest;
import org.testng.annotations.Test;

public class PageContextTest extends SeamTest
{

   @Test
   public void pageContextTest() throws Exception {

      new FacesRequest("/index.xhtml") {
          
         @Override
         protected void invokeApplication() throws Exception
         {
            Contexts.getPageContext().set("foo", "bar");
            assert Contexts.getPageContext().get("foo") == null;
         }
         
         @Override
         protected void renderResponse() throws Exception
         {
             assert Contexts.getPageContext().get("foo") != null;
             assert "bar".equals(Contexts.getPageContext().get("foo"));
         }
      }.run();
      
  } 
   
}

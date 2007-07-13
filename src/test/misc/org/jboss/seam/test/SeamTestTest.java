package org.jboss.seam.test;

import javax.faces.el.MethodBinding;

import org.jboss.seam.jsf.UnifiedELMethodBinding;
import org.jboss.seam.mock.SeamTest;
import org.testng.annotations.Test;

public class SeamTestTest extends SeamTest
{
   
   @Override
   protected void startJbossEmbeddedIfNecessary() 
   throws org.jboss.deployers.spi.DeploymentException ,java.io.IOException {}
   
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
            setValue("#{person.name}", PETER_NAME);
         }
         
         @Override
         protected void renderResponse() throws Exception
         {
            assert getValue("#{person.name}").equals(PETER_NAME);
         }
         
         @Override
         protected void invokeApplication() throws Exception
         {
            MethodBinding methodBinding = new UnifiedELMethodBinding("#{action.go}", new Class[0]);
            Object result = methodBinding.invoke(getFacesContext(), new Object[0]);
            
            assert result instanceof String;
            assert "success".equals(result);
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

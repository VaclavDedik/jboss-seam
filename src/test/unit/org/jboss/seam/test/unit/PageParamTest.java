package org.jboss.seam.test.unit;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.IntegerConverter;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.jboss.seam.mock.MockApplication;
import org.jboss.seam.mock.MockExternalContext;
import org.jboss.seam.mock.MockFacesContext;
import org.jboss.seam.navigation.Param;
import org.testng.annotations.Test;

public class PageParamTest
{
   @Test
   public void testGetConverterById()
   {
      String converterId = "javax.faces.Integer";
      String converterClass = "javax.faces.convert.IntegerConverter";
      MockFacesContext facesContext = new MockFacesContext(new MockExternalContext(), new MockApplication());
      facesContext.setCurrent();
      Param param = new Param("param");
      param.setConverterId(converterId);
      assert param.getConverter() instanceof IntegerConverter : "expecting: " + converterClass + "; got: " + param.getConverter();
   }
   
   @Test
   public void testGetValidatorById() throws ClassNotFoundException
   {
      String validatorId = "TestValidator";
      String validatorClass= "org.jboss.seam.test.unit.PageParamTest$TestValidator";
      MockFacesContext facesContext = new MockFacesContext(new MockExternalContext(), new MockApplication());
      facesContext.setCurrent();
      facesContext.getApplication().addValidator(validatorId, validatorClass);
      Param param = new Param("param");
      param.setValidatorId(validatorId);
      assert param.getValidator() instanceof TestValidator : "expecting: " + validatorClass + "; got: " + param.getValidator();
   }
   
   public static class TestValidator implements Validator
   {
      public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException
      {
      }
      
   }
}

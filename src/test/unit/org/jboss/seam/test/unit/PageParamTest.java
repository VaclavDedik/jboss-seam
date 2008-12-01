package org.jboss.seam.test.unit;

import java.util.HashMap;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.IntegerConverter;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.jsf.SeamApplication;
import org.jboss.seam.mock.MockApplication;
import org.jboss.seam.mock.MockExternalContext;
import org.jboss.seam.mock.MockFacesContext;
import org.jboss.seam.navigation.Param;
import org.testng.annotations.Test;

/**
 * Verifies that page parameters are setup properly and report the correct information
 * about validators and converters.
 * 
 * @author Dan Allen
 */
public class PageParamTest
{
   @Test
   public void testGetConverterById()
   {
      String converterId = "javax.faces.Integer";
      String converterClass = "javax.faces.convert.IntegerConverter";
      setupMockFacesContextWithSeamApplication();
      Param param = new Param("param");
      param.setConverterId(converterId);
      assert param.getConverter() instanceof IntegerConverter : "expecting: " + converterClass + "; got: " + param.getConverter();
   }
   
   /**
    * Verify that converter is null when the parameter value is a value expression and
    * we are operating outside of a FacesContext.
    * @jira JBSEAM-3674
    */
   @Test
   public void testConverterIsNullForNonFacesValueExpression()
   {
      setupMockFacesContextWithSeamApplication();
      Param param = new Param("param");
      param.setValueExpression(Expressions.instance().createValueExpression("#{variable}"));
      Lifecycle.beginApplication(new HashMap<String, Object>());
      Lifecycle.mockApplication();
      assert param.getConverter() == null;
      Lifecycle.endApplication();
   }
   
   @Test
   public void testGetValidatorById() throws ClassNotFoundException
   {
      String validatorId = "TestValidator";
      String validatorClass= "org.jboss.seam.test.unit.PageParamTest$TestValidator";
      FacesContext facesContext = setupMockFacesContextWithSeamApplication();
      facesContext.getApplication().addValidator(validatorId, validatorClass);
      Param param = new Param("param");
      param.setValidatorId(validatorId);
      assert param.getValidator() instanceof TestValidator : "expecting: " + validatorClass + "; got: " + param.getValidator();
   }
   
   protected FacesContext setupMockFacesContextWithSeamApplication()
   {
      // MockApplication is wrapped with SeamApplication to validate the behavior Seam introduces
      return new MockFacesContext(new MockExternalContext(), new SeamApplication(new MockApplication())).setCurrent();
   }
   
   public static class TestValidator implements Validator
   {
      public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException
      {
      }
   }
}

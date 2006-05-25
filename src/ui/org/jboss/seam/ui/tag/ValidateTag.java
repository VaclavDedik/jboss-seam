package org.jboss.seam.ui.tag;

import javax.faces.validator.Validator;
import javax.faces.webapp.ValidatorTag;
import javax.servlet.jsp.JspException;

public class ValidateTag extends ValidatorTag
{
   private static final String VALIDATOR_ID = "org.jboss.seam.ui.ModelValidator";

   @Override
   protected Validator createValidator() throws JspException
   {
      setValidatorId(VALIDATOR_ID);
      return super.createValidator();
   }
   
   
}

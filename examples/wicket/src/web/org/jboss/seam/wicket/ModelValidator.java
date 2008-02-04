package org.jboss.seam.wicket;

import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidationError;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;
import org.hibernate.validator.ClassValidator;
import org.hibernate.validator.InvalidValue;
import org.jboss.seam.core.Validators;


/**
 * 
 * An implementation of Hibernate Model Validation for Wicket
 * 
 * @author Pete Muir
 *
 */
public class ModelValidator implements IValidator
{

   private Class clazz;
   private String property;

   public ModelValidator(Class clazz, String property)
   {
      this.clazz = clazz;
      this.property = property;
   }
   
   public ModelValidator(PropertyModel propertyModel)
   {
      this.clazz = propertyModel.getTarget().getClass();
      this.property = propertyModel.getPropertyExpression();
   }
   
   public void validate(IValidatable validatable)
   {
	   System.out.println("model validator " + property + " / " + clazz);
      ClassValidator classValidator = Validators.instance().getValidator(clazz);
      InvalidValue[] invalidValues = classValidator.getPotentialInvalidValues(property, validatable.getValue());
      if (invalidValues.length > 0)
      {
         String message = invalidValues[0].getMessage();
         IValidationError validationError = new ValidationError().setMessage(message);
         validatable.error(validationError);
      }
   }

}

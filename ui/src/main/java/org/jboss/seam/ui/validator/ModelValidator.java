package org.jboss.seam.ui.validator;

import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.hibernate.validator.InvalidValue;
import org.jboss.seam.core.Validators;
import org.jboss.seam.faces.FacesMessages;

/**
 * Validates using Hibernate Validator model-based annotations.
 * 
 * @author Gavin King
 * @author Jacob Hookom
 *
 */
public class ModelValidator implements Validator
{

   public void validate(FacesContext facesContext, UIComponent component, Object value)
            throws ValidatorException
   {
      ValueExpression valueExpression = component.getValueExpression("value");
      if (valueExpression != null)
      {
         //TODO: note that this code is duplicated to Param.getValueFromRequest()!!
         InvalidValue[] invalidValues;
         try
         {
            invalidValues = Validators.instance().validate( valueExpression, facesContext.getELContext(), value );
         }
         catch (ELException ele)
         {
            Throwable cause = ele.getCause();
            if (cause==null) cause = ele;
            throw new ValidatorException( createMessage(cause), cause );
         }
         
         if ( invalidValues!=null && invalidValues.length>0 )
         {
            throw new ValidatorException( createMessage(invalidValues) );
         }
      }
   }

   private FacesMessage createMessage(InvalidValue[] invalidValues)
   {
      return FacesMessages.createFacesMessage( FacesMessage.SEVERITY_ERROR, invalidValues[0].getMessage() );
   }

   private FacesMessage createMessage(Throwable cause)
   {
      return new FacesMessage(FacesMessage.SEVERITY_ERROR, "model validation failed:" + cause.getMessage(), null);
   }

}

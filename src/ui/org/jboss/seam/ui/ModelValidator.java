package org.jboss.seam.ui;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.hibernate.validator.InvalidValue;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.core.FacesMessages;

public class ModelValidator implements Validator
{

   public void validate(FacesContext context, UIComponent component, Object value)
         throws ValidatorException
   {
      ValueBinding valueBinding = component.getValueBinding("value");
      if (valueBinding==null)
      {
         throw new RuntimeException("component has no value attribute: " + component.getId());
      }
      InvalidValue[] ivs;
      try
      {
         ivs = Expressions.instance().validate( valueBinding.getExpressionString(), value );
      }
      catch (Exception e)
      {
         throw new ValidatorException( new FacesMessage(FacesMessage.SEVERITY_ERROR, "model validation failed:" + e.getMessage(), null) );
      }
      if ( ivs.length>0 )
      {
         throw new ValidatorException( FacesMessages.createFacesMessage( FacesMessage.SEVERITY_WARN, ivs[0].getMessage() ) );
      }
   }

}

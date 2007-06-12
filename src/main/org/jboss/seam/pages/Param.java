/**
 * 
 */
package org.jboss.seam.pages;

import java.util.Map;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.jboss.seam.core.Expressions;
import org.jboss.seam.core.Expressions.ValueExpression;

public final class Param
{
   private final String name;
   private ValueExpression valueExpression;
   
   private boolean required;
   
   private ValueExpression converterValueExpression;
   private String converterId;
   
   private ValueExpression validatorValueExpression;
   private String validatorId;
   
   public Param(String name)
   {
      this.name = name;
   }
   
   public Converter getConverter()
   {
      if (converterId!=null)
      {
         return FacesContext.getCurrentInstance().getApplication().createConverter(converterId);
      }
      else if (converterValueExpression!=null)
      {
         return (Converter) converterValueExpression.getValue();
      }
      else if (valueExpression==null)
      {
         return null;
      }
      else
      {
         Class<?> type = valueExpression.getType();
         return FacesContext.getCurrentInstance().getApplication().createConverter(type);           
      }
   }

   public Validator getValidator()
   {
      if (validatorId!=null)
      {
         return FacesContext.getCurrentInstance().getApplication().createValidator(converterId);
      }
      else if (validatorValueExpression!=null)
      {
         return (Validator) validatorValueExpression.getValue();
      }
      else
      {
         return null;
      }
   }

   public String getName()
   {
      return name;
   }

   public void setValueExpression(ValueExpression valueExpression)
   {
      this.valueExpression = valueExpression;
   }

   public ValueExpression getValueExpression()
   {
      return valueExpression;
   }

   public void setConverterValueExpression(ValueExpression converterValueExpression)
   {
      this.converterValueExpression = converterValueExpression;
   }

   public ValueExpression getConverterValueExpression()
   {
      return converterValueExpression;
   }

   public void setConverterId(String converterId)
   {
      this.converterId = converterId;
   }

   public String getConverterId()
   {
      return converterId;
   }

   @Override
   public String toString()
   {
      return "PageParameter(" + name + ")";
   }

   /**
    * Get the current value of a page or redirection parameter
    * from the model, and convert to a String
    */
   public Object getValueFromModel(FacesContext facesContext)
   {
      Object value = getValueExpression().getValue();
      if (value==null)
      {
         return null;
      }
      else
      {
         Converter converter = null;
         try
         {
            converter = getConverter();
         }
         catch (RuntimeException re)
         {
            //YUCK! due to bad JSF/MyFaces error handling
            return null;
         }
         
         return converter==null ? 
               value : 
               converter.getAsString( facesContext, facesContext.getViewRoot(), value );
      }
   }

   /**
    * Get the current value of a page parameter from the request parameters
    */
   public Object getValueFromRequest(FacesContext facesContext, Map<String, String[]> requestParameters)
            throws ValidatorException
   {
      String[] parameterValues = requestParameters.get( getName() );

      if (parameterValues==null || parameterValues.length==0)
      {
         if ( isRequired() )
         {
            addRequiredMessage(facesContext);
         }
         return null;
      }

      if (parameterValues.length>1)
      {
         throw new IllegalArgumentException("page parameter may not be multi-valued: " + getName());
      }         

      String stringValue = parameterValues[0];
      
      //Note: for not-required fields, we behave a
      //but different than JSF for empty strings.
      //is this a bad thing? (but we are the same
      //for required fields)
      if ( stringValue.length()==0 && isRequired() )
      {
         addRequiredMessage(facesContext);
         return null;
      }
   
      Converter converter = null;
      try
      {
         converter = getConverter();
      }
      catch (RuntimeException re)
      {
         //YUCK! due to bad JSF/MyFaces error handling
         return null;
      }
      
      Object value = converter==null ? 
            stringValue :
            converter.getAsObject( facesContext, facesContext.getViewRoot(), stringValue );
      
      Validator validator = getValidator();
      if (validator!=null)
      {
         validator.validate( facesContext, facesContext.getViewRoot(), value );
      }
      
      Expressions.instance().validate( valueExpression.getExpressionString(), value );
      
      return value;
   }

   private void addRequiredMessage(FacesContext facesContext)
   {
      String bundleName = facesContext.getApplication().getMessageBundle();
      if (bundleName==null) bundleName = FacesMessage.FACES_MESSAGES;
      ResourceBundle resourceBundle = facesContext.getApplication().getResourceBundle(facesContext, bundleName);
      //TODO: this should not be necessary!
      if (resourceBundle==null)
      {
         resourceBundle = org.jboss.seam.core.ResourceBundle.instance();
      }
      throw new ValidatorException( new FacesMessage(
               FacesMessage.SEVERITY_ERROR, 
               resourceBundle.getString("javax.faces.component.UIInput.REQUIRED"), 
               resourceBundle.getString("javax.faces.component.UIInput.REQUIRED_detail")
            ) );
   }

   public String getValidatorId()
   {
      return validatorId;
   }

   public void setValidatorId(String validatorId)
   {
      this.validatorId = validatorId;
   }

   public ValueExpression getValidatorValueExpression()
   {
      return validatorValueExpression;
   }

   public void setValidatorValueExpression(ValueExpression validatorValueExpression)
   {
      this.validatorValueExpression = validatorValueExpression;
   }

   public boolean isRequired()
   {
      return required;
   }

   public void setRequired(boolean required)
   {
      this.required = required;
   }
   
}
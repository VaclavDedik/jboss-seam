package org.jboss.seam.ui.validator;

import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.render.Renderer;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

/**
 * Validate two fields are equal
 * 
 * @author pmuir
 *
 */
public class EqualityValidator implements Validator, StateHolder
{
   
   private static LogProvider log = Logging.getLogProvider(EqualityValidator.class);
   
   public static final String MESSAGE_ID = "org.jboss.seam.ui.validator.NOT_EQUAL";
   
   public static final String VALIDATOR_ID = "org.jboss.seam.ui.validator.Equality";

   private String forId;
   
   private String message;
   private String messageId;
   
   public EqualityValidator() 
   {
      this.message = "Value does not equal that in #0";
      this.messageId = MESSAGE_ID;
   }
   
   public EqualityValidator(String forId)
   {
      this();
      setFor(forId);
   }
   
   public EqualityValidator(String forId, String message, String messageId)
   {
      this(forId);
      if (message != null)
      {
         setMessage(message);
      }
      if (messageId != null)
      {
         setMessageId(messageId);
      }
   }
   
   public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException
   {
      if (!(component instanceof EditableValueHolder))
      {
         throw new FacesException("Must attach an equality validator to an input component");
      }
      String forId = getFor();
      if (forId == null)
      {
         throw new FacesException("Must specify a component to validate equality against");
      }
      UIComponent otherComponent = component.findComponent(forId);
      Object other = new OtherComponent(context, otherComponent).getValue();
      if (value == null && other == null)
      {
         // Thats fine
      }
      else if (value != null)
      {
         if (!value.equals(other))
         {
            String otherComponentId = otherComponent.getId();
            throw new ValidatorException(FacesMessages.createFacesMessage(javax.faces.application.FacesMessage.SEVERITY_ERROR, getMessageId(), getMessage(), otherComponentId, value, other));
         }
      }
   }
   
   public String getFor()
   {
      return forId;
   } 
   
   public void setFor(String forId)
   {
      this.forId = forId;
   }
   
   public String getMessage()
   {
      return message;
   }
   
   public void setMessage(String message)
   {
      this.message = message;
   }
   
   public String getMessageId()
   {
      return messageId;
   }
   
   public void setMessageId(String messageId)
   {
      this.messageId = messageId;
   }

   public boolean isTransient()
   {
      return false;
   }

   public void restoreState(FacesContext context, Object state)
   {
      Object[] fields = (Object []) state;
      forId = (String) fields[0];
      message = (String) fields[1];
      messageId = (String) fields[2];
   }

   public Object saveState(FacesContext context)
   {
      Object[] state = new Object[3];
      state[0] = forId;
      state[1] = message;
      state[2] = messageId;
      return state;
   }

   public void setTransient(boolean newTransientValue)
   {
      // No-op
   }

   
   /**
    * Simple data strcuture to hold info on the "other" component
    * @author pmuir
    *
    */
   private class OtherComponent 
   {
      
      private FacesContext context;
      private UIComponent component;
      private EditableValueHolder editableValueHolder;
      
      private Renderer renderer;
      private Converter converter;
      
      public OtherComponent(FacesContext facesContext, UIComponent component)
      {
         this.component = component;
         this.context = facesContext;
         if (!(component instanceof EditableValueHolder))
         {
            throw new IllegalStateException("forId must reference an EditableValueHolder (\"input\") component");
         }
         editableValueHolder = (EditableValueHolder) component;
         initRenderer();
         initConverter();
      }
      
      private void initRenderer() 
      {
         if (renderer == null)
         {
            String rendererType = component.getRendererType();
            if (rendererType != null) 
            {
               renderer = context.getRenderKit().getRenderer(component.getFamily(), rendererType);
               if (null == renderer) 
               {
                  log.trace("Can't get Renderer for type " + rendererType);
               }
            } 
            else
            {
               if (log.isTraceEnabled()) 
               {
                  String id = component.getId();
                  id = (null != id) ? id : component.getClass().getName();
                  log.trace("No renderer-type for component " + id);
               }
            }
         }
      }
      
      private void initConverter() {
         converter = editableValueHolder.getConverter();
         if (converter != null) {
             return;
         }

         ValueExpression valueExpression = component.getValueExpression("value");
         if (valueExpression == null) {
             return;
         }

         Class converterType;
         try {
             converterType = valueExpression.getType(context.getELContext());
         }
         catch (ELException e) {
             throw new FacesException(e);
         }

         // if converterType is null, String, or Object, assume
         // no conversion is needed
         if (converterType == null || converterType == String.class || converterType == Object.class) 
         {
             return;
         }

         // if getType returns a type for which we support a default
         // conversion, acquire an appropriate converter instance.
         try 
         {
             Application application = context.getApplication();
             converter = application.createConverter(converterType);
         }
         catch (Exception e) 
         {
            throw new FacesException(e);
         }
      }
      
      private Object getConvertedValue(Object newSubmittedValue) throws ConverterException 
      {
         
         Object newValue;

         if (renderer != null) 
         {
            newValue = renderer.getConvertedValue(context, component, newSubmittedValue);
         } 
         else if (newSubmittedValue instanceof String) 
         {
            // If there's no Renderer, and we've got a String, run it through the Converter (if any)
            if (converter != null) {
               newValue = converter.getAsObject(context, component,
                     (String) newSubmittedValue);
            } 
            else
            {
               newValue = newSubmittedValue;
            }
         } 
         else 
         {
            newValue = newSubmittedValue;
         }
         return newValue;
      }
      
      public Object getValue()
      {
         Object submittedValue = editableValueHolder.getLocalValue();
         if (submittedValue == null) 
         {
            return null;
         }

         Object newValue = null;

         try 
         {
            newValue = getConvertedValue(submittedValue);
         }
         catch (ConverterException ce) 
         {
            // Any errors will be attached by JSF
            return null;
         }

         return newValue;
      }
      
   }
}

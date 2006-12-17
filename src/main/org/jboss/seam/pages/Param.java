/**
 * 
 */
package org.jboss.seam.pages;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.jboss.seam.core.Expressions.ValueBinding;

public final class Param
{
   private final String name;
   private ValueBinding valueBinding;
   private ValueBinding converterValueBinding;
   private String converterId;
   
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
      else if (converterValueBinding!=null)
      {
         return (Converter) converterValueBinding.getValue();
      }
      else if (valueBinding==null)
      {
         return null;
      }
      else
      {
         Class<?> type = valueBinding.getType();
         return FacesContext.getCurrentInstance().getApplication().createConverter(type);           
      }
   }

   public String getName()
   {
      return name;
   }

   public void setValueBinding(ValueBinding valueBinding)
   {
      this.valueBinding = valueBinding;
   }

   public ValueBinding getValueBinding()
   {
      return valueBinding;
   }

   public void setConverterValueBinding(ValueBinding converterValueBinding)
   {
      this.converterValueBinding = converterValueBinding;
   }

   public ValueBinding getConverterValueBinding()
   {
      return converterValueBinding;
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
      Object value = getValueBinding().getValue();
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
   {
      String[] parameterValues = requestParameters.get( getName() );
      if (parameterValues==null || parameterValues.length==0)
      {
         return null;
      }
      if (parameterValues.length>1)
      {
         throw new IllegalArgumentException("page parameter may not be multi-valued: " + getName());
      }         
      String stringValue = parameterValues[0];
   
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
            stringValue :
            converter.getAsObject( facesContext, facesContext.getViewRoot(), stringValue );
   }

}
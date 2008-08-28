package org.jboss.seam.pdf.ui;

import java.io.IOException;

import javax.faces.context.FacesContext;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Interpolator;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.AcroFields;

public class UIField extends FormComponent
{
   public static final String COMPONENT_FAMILY = "org.jboss.seam.pdf.UIField";
   
   private String name;
   private String value;
   
   @Override
   public void encodeBegin(FacesContext facesContext) throws IOException
   {
      AcroFields fields = (AcroFields) Contexts.getEventContext().get(FIELDS_KEY);
      try
      {
         fields.setField(getName(), getValue());
      }
      catch (DocumentException e)
      {
         String message = Interpolator.instance().interpolate("Could not set field #0 to #1", getName(), getValue());
         throw new IOException(message);
      }
   }

   @Override
   public String getFamily()
   {
      return COMPONENT_FAMILY;
   }

   public String getName()
   {
      return (String) valueOf("name", name);
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public String getValue()
   {
      return (String) valueOf("value", value);
   }

   public void setValue(String value)
   {
      this.value = value;
   }

}

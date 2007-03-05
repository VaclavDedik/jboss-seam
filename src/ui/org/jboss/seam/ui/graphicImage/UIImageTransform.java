package org.jboss.seam.ui.graphicImage;

import javax.faces.component.UIComponentBase;
import javax.faces.el.ValueBinding;

import org.jboss.seam.ui.JSF;

public class UIImageTransform extends UIComponentBase
{
 
   public static final String SCALE = "scale";
   
   public static final String TYPE = "contentType";
   
   private String type; 
   
   public static final String FAMILY = "org.jboss.seam.ui.UIGraphicImage";

   @Override
   public String getFamily()
   {
      return FAMILY;
   }
   
   public void setType(String type)
   {
      this.type = type;
   }
   
   public String getType()
   {
      if (type != null) 
      {
         return type;
      }
      else 
      {
         ValueBinding vb = getValueBinding("type");
         return vb == null ? null : JSF.getStringValue(getFacesContext(), vb);
      }
   }

}

package org.jboss.seam.ui.graphicImage;

import java.io.IOException;

import javax.faces.component.UIComponentBase;
import javax.faces.el.ValueBinding;

import org.jboss.seam.core.Image;
import org.jboss.seam.ui.JSF;

public class UITransformImageType extends UIComponentBase implements ImageTransform
{
   
   private String contentType;
   
 
   @Override
   public String getFamily()
   {
      return FAMILY;
   }
   
   public void applyTransform(Image image, UIGraphicImage cmp) throws IOException
   {
      Image.Type type = Image.Type.getTypeByMimeType(getContentType());
      if (type != null)
      {
         image.setContentType(type);
      }
   }
   
   public String getContentType()
   {
      if (contentType != null)
      {
         return contentType;
      }
      else
      {
         ValueBinding vb = getValueBinding("width");
         return vb == null ? null : JSF.getStringValue(getFacesContext(), vb);
      }
   }
   
   public void setContentType(String width)
   {
      this.contentType = width;
   }
   
}

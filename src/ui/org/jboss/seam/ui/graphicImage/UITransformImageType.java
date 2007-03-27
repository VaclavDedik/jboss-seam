package org.jboss.seam.ui.graphicImage;

import java.io.IOException;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
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
      if (!isRendered())
      {
         return;
      }
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
   
   @Override
   public Object saveState(FacesContext context)
   {
      Object[] state = new Object[2];
      state[0] = super.saveState(context);
      state[1] = contentType;
      return state;
   }
   
   @Override
   public void restoreState(FacesContext context, Object state)
   {
      Object[] objects = (Object[]) state;
      super.restoreState(context, objects[0]);
      contentType = (String) objects[1];
   }
   
}

package org.jboss.seam.ui.graphicImage;

import java.io.IOException;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

import org.jboss.seam.core.Image;
import org.jboss.seam.ui.JSF;

public class UITransformImageBlur extends UIComponentBase implements ImageTransform
{
   @Override
   public String getFamily()
   {
      return FAMILY;
   }
   
   private String radius;
   
   public void applyTransform(Image image) throws IOException
   {
      if (!isRendered())
      {
         return;
      }
      image.blur(new Integer(getRadius()));
   }
   
   public String getRadius()
   {
      if (radius != null)
      {
         return radius;
      }
      else
      {
         ValueBinding vb = getValueBinding("width");
         return vb == null ? null : JSF.getStringValue(getFacesContext(), vb);
      }
   }
   
   public void setRadius(String width)
   {
      this.radius = width;
   }
   
   @Override
   public Object saveState(FacesContext context)
   {
      Object[] state = new Object[2];
      state[0] = super.saveState(context);
      state[1] = radius;
      return state;
   }
   
   @Override
   public void restoreState(FacesContext context, Object state)
   {
      Object[] objects = (Object[]) state;
      super.restoreState(context, objects[0]);
      radius = (String) objects[1];
   }
   
}

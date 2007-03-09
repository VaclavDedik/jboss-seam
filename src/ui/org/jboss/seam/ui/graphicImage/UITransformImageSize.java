package org.jboss.seam.ui.graphicImage;

import java.io.IOException;

import javax.faces.component.UIComponentBase;
import javax.faces.el.ValueBinding;

import org.jboss.seam.core.Image;
import org.jboss.seam.ui.JSF;

public class UITransformImageSize extends UIComponentBase implements ImageTransform
{
   
   private Boolean maintainRatio;
   
   private String width;
   
   private String height;
   
   private String factor;
   
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
//    TODO reduce number of decimal places
      if (isMaintainRatio())
      {
         if (getWidth() != null && getHeight() != null)
         {
            throw new UnsupportedOperationException("Cannot maintain ratio and specify height and width");
         }
         else if (getWidth() != null)
         {
            image.scaleToWidth(new Integer(getWidth()));
         }
         else if (getHeight() != null)
         {
            image.scaleToHeight(new Integer(getHeight()));
         }
      }
      else if (getFactor() != null)
      {
         if (getWidth() != null || getHeight() != null)
         {
            throw new UnsupportedOperationException("Cannot scale by a factor and specify height and width");
         }
         image.scale(new Double(factor));
      }
      else
      {
         image.resize(new Integer(getWidth()), new Integer(getHeight()));
      }
   }
   
   public boolean isMaintainRatio()
   {
      if (maintainRatio != null)
      {
         return maintainRatio;
      }
      else 
      {
         ValueBinding vb = getValueBinding("maintainRatio");
         Boolean b = vb == null ? null : JSF.getBooleanValue(getFacesContext(), vb);
         return b == null ? false : b;
      }
   }
   
   public void setMaintainRatio(boolean maintainRatio)
   {
      this.maintainRatio = maintainRatio;
   }
   
   public String getWidth()
   {
      if (width != null)
      {
         return width;
      }
      else
      {
         ValueBinding vb = getValueBinding("width");
         return vb == null ? null : JSF.getStringValue(getFacesContext(), vb);
      }
   }
   
   public void setWidth(String width)
   {
      this.width = width;
   }
   
   public String getHeight()
   {
      if (height != null)
      {
         return height;
      }
      else
      {
         ValueBinding vb = getValueBinding("height");
         return vb == null ? null : JSF.getStringValue(getFacesContext(), vb);
      }
   }
   
   public void setHeight(String height)
   {
      this.height = height;
   }
   
   public String getFactor()
   {
      if (factor != null)
      {
         return factor;
      }
      else
      {
         ValueBinding vb = getValueBinding("factor");
         return vb == null ? null : JSF.getStringValue(getFacesContext(), vb);
      }
   }
   
   public void setFactor(String factor)
   {
      this.factor = factor;
   }
   
}

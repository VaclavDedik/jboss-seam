package org.jboss.seam.ui.graphicImage;

import java.io.IOException;

import javax.el.ValueExpression;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

import org.jboss.seam.core.Image;
import org.jboss.seam.ui.JSF;

public class UITransformImageSize extends UIComponentBase implements ImageTransform
{
   
   private Boolean maintainRatio;
   
   private Integer width;
   
   private Integer height;
   
   private Double factor;
   
   @Override
   public String getFamily()
   {
      return FAMILY;
   }
   
   public void applyTransform(Image image) throws IOException
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
         ValueExpression vb = getValueExpression("maintainRatio");
         Boolean b = vb == null ? null : JSF.getBooleanValue(getFacesContext(), vb);
         return b == null ? false : b;
      }
   }
   
   public void setMaintainRatio(boolean maintainRatio)
   {
      this.maintainRatio = maintainRatio;
   }
   
   public Integer getWidth()
   {
      if (width != null)
      {
         return width;
      }
      else
      {
         ValueExpression vb = getValueExpression("width");
         return vb == null ? null : JSF.getIntegerValue(getFacesContext(), vb);
      }
   }
   
   public void setWidth(Integer width)
   {
      this.width = width;
   }
   
   public Integer getHeight()
   {
      if (height != null)
      {
         return height;
      }
      else
      {
         ValueExpression vb = getValueExpression("height");
         return vb == null ? null : JSF.getIntegerValue(getFacesContext(), vb);
      }
   }
   
   public void setHeight(Integer height)
   {
      this.height = height;
   }
   
   public Double getFactor()
   {
      if (factor != null)
      {
         return factor;
      }
      else
      {
         ValueExpression vb = getValueExpression("factor");
         return vb == null ? null : JSF.getDoubleValue(getFacesContext(), vb);
      }
   }
   
   public void setFactor(Double factor)
   {
      this.factor = factor;
   }

   @Override
   public Object saveState(FacesContext context)
   {
      Object[] state = new Object[5];
      state[0] = super.saveState(context);
      state[1] = factor;
      state[2] = height;
      state[3] = width;
      state[4] = maintainRatio;
      return state;
   }
   
   @Override
   public void restoreState(FacesContext context, Object state)
   {
      Object[] objects = (Object[]) state;
      super.restoreState(context, objects[0]);
      factor = (Double) objects[1];
      height = (Integer) objects[2];
      width = (Integer) objects[3];
      maintainRatio = (Boolean) objects[4];
   }
   
}

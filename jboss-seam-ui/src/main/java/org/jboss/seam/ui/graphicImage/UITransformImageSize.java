package org.jboss.seam.ui.graphicImage;

import java.io.IOException;

import javax.faces.component.UIComponentBase;

import org.richfaces.cdk.annotations.Attribute;
import org.richfaces.cdk.annotations.Description;
import org.richfaces.cdk.annotations.JsfComponent;
import org.richfaces.cdk.annotations.Tag;

/**
 * 
 * JSF Component which nestes in a &lt;s:graphicImage&gt;.Transforms an image by altering the size.
 * 
 * @author mnovotny
 *
 */
@JsfComponent(description=@Description(displayName="org.jboss.seam.ui.graphicImage.TransformImageSize",value="Nested in a s:graphicImage. Transform an image by altering the size."),
family="org.jboss.seam.ui.graphicImage.TransformImageSize", type="org.jboss.seam.ui.graphicImage.TransformImageSize",generate="org.jboss.seam.ui.component.html.HtmlTransformImageSize", 
tag = @Tag(baseClass="org.jboss.seam.ui.util.cdk.UIComponentTagBase", name="transformImageSize"))
public abstract class UITransformImageSize extends UIComponentBase implements ImageTransform
{

   public void applyTransform(Image image) throws IOException
   {
      if (!isRendered())
      {
         return;
      }
      // TODO reduce number of decimal places
      if (isMaintainRatio())
      {
         if (getWidth() != null && getHeight() != null)
         {
            throw new UnsupportedOperationException(
                     "Cannot maintain ratio and specify height and width");
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
            throw new UnsupportedOperationException(
                     "Cannot scale by a factor and specify height and width");
         }
         image.scale(getFactor());
      }
      else
      {
         if (getWidth() == null || getHeight() == null)
         {
            throw new UnsupportedOperationException(
            "If not specifying a factor or maintain ratio you must specify width and heigh");
         }
         image.resize(new Integer(getWidth()), new Integer(getHeight()));
      }
   }

   @Attribute(description = @Description("If true, don't alter the ratio of the image. In this case only height or width should be specificed."))
   public abstract boolean isMaintainRatio();

   public abstract void setMaintainRatio(boolean maintainRatio);

   @Attribute(description = @Description("The new width of the image"))
   public abstract Integer getWidth();

   public abstract void setWidth(Integer width);

   @Attribute(description = @Description("The new height of the image"))
   public abstract Integer getHeight();

   public abstract void setHeight(Integer height);

   @Attribute(description = @Description("Change the size of the image by a fraction. If factor is specified, " +
           "height, width and maintainRatio should not be specified"))
   public abstract Double getFactor();

   public abstract void setFactor(Double factor);

}

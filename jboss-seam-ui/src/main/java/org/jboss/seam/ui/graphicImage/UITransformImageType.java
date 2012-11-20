package org.jboss.seam.ui.graphicImage;

import java.io.IOException;

import javax.faces.component.UIComponentBase;

import org.richfaces.cdk.annotations.Attribute;
import org.richfaces.cdk.annotations.Description;
import org.richfaces.cdk.annotations.JsfComponent;
import org.richfaces.cdk.annotations.Tag;

/**
 * JSF Component which nestes in a &lt;s:graphicImage&gt;.Transform an image by changing it's type.
 * 
 * @author mnovotny
 *
 */
@JsfComponent(description=@Description(displayName="org.jboss.seam.ui.graphicImage.TransformImageType",value="Nested in a s:graphicImage. Transform an image by changing it's type."),
family="org.jboss.seam.ui.graphicImage.TransformImageType", type="org.jboss.seam.ui.graphicImage.TransformImageType",generate="org.jboss.seam.ui.component.html.HtmlTransformImageType", 
tag = @Tag(baseClass="org.jboss.seam.ui.util.cdk.UIComponentTagBase", name="transformImageType"))
public abstract class UITransformImageType extends UIComponentBase implements ImageTransform
{
   
   public void applyTransform(Image image) throws IOException
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
   
   @Attribute(description = @Description("The mime type of the output image"))
   public abstract String getContentType();
   
   public abstract void setContentType(String width);
   
}

package org.jboss.seam.ui.graphicImage;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.ajax4jsf.framework.renderer.AjaxComponentRendererBase;
import org.ajax4jsf.framework.renderer.RendererUtils;
import org.jboss.seam.core.Image;
import org.jboss.seam.ui.graphicImage.GraphicImageStore.ImageWrapper;

public class GraphicImageRendererBase extends AjaxComponentRendererBase
{
   
   private static final String IMG_ELEM = "img";
   private static final String SRC_ATTR = "src";
   private static final String HSPACE_ATTR = "hspace";
   private static final String ISMAP_ATTR = "ismap";
   public static final String VSPACE_ATTR = "vspace";
   
   private static final String[] IMG_PASSTHROUGH_ATTRIBUTES =
   {
      RendererUtils.HTML.align_ATTRIBUTE,
      RendererUtils.HTML.alt_ATTRIBUTE,
      RendererUtils.HTML.border_ATTRIBUTE,
      RendererUtils.HTML.height_ATTRIBUTE,
      HSPACE_ATTR,
      ISMAP_ATTR,
      RendererUtils.HTML.longdesc_ATTRIBUTE,
      RendererUtils.HTML.usemap_ATTRIBUTE,
      VSPACE_ATTR,
      RendererUtils.HTML.width_ATTRIBUTE,
      RendererUtils.HTML.ondblclick_ATTRIBUTE,
      RendererUtils.HTML.onmousedown_ATTRIBUTE,
      RendererUtils.HTML.onmouseup_ATTRIBUTE,
      RendererUtils.HTML.onmouseover_ATTRIBUTE,
      RendererUtils.HTML.onmousemove_ATTRIBUTE,
      RendererUtils.HTML.onmouseout_ATTRIBUTE,
      RendererUtils.HTML.onkeypress_ATTRIBUTE,
      RendererUtils.HTML.onkeydown_ATTRIBUTE,
      RendererUtils.HTML.onkeyup_ATTRIBUTE,
      RendererUtils.HTML.dir_ATTRIBUTE,
      RendererUtils.HTML.lang_ATTRIBUTE,
      RendererUtils.HTML.title_ATTRIBUTE,
      RendererUtils.HTML.style_ATTRIBUTE,
      RendererUtils.HTML.STYLE_CLASS_ATTR
   };
   
   @Override
   protected Class getComponentClass()
   {
      return UIGraphicImage.class;
   }
   
   @Override
   protected void doEncodeBegin(ResponseWriter writer, FacesContext context, UIComponent component) throws IOException
   {
      UIGraphicImage graphicImage = (UIGraphicImage) component;
      
      String key = graphicImage.getFileName();
      String extension = null;

      Image image = Image.instance();
      image.setInput(graphicImage.getValue());
      
      // Do transforms
      
      for (UIComponent cmp : graphicImage.getChildren()) 
      {
         if (cmp instanceof ImageTransform)
         {
            ImageTransform imageTransform = (ImageTransform) cmp;
            imageTransform.applyTransform(image);
         }
      }

      key = GraphicImageStore.instance().put(new ImageWrapper(image.getImage(), image.getContentType()),
               key);
      extension = image.getContentType().getExtension();

      writer.startElement(IMG_ELEM, graphicImage);
      String url = context.getExternalContext().getRequestContextPath()
               + GraphicImageResource.GRAPHIC_IMAGE_RESOURCE_PATH + "/" + key + extension;
      writer.writeAttribute(SRC_ATTR, url, SRC_ATTR);
      
      new RendererUtils().encodeAttributesFromArray(context, component, IMG_PASSTHROUGH_ATTRIBUTES);
      writer.endElement(IMG_ELEM);
   }
   
   @Override
   public boolean getRendersChildren()
   {
      return true;
   }

}

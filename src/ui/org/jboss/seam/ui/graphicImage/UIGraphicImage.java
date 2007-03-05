package org.jboss.seam.ui.graphicImage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.component.html.HtmlGraphicImage;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.el.ValueBinding;
import javax.faces.render.Renderer;

import org.jboss.seam.core.Image;
import org.jboss.seam.ui.HTML;
import org.jboss.seam.ui.JSF;
import org.jboss.seam.ui.graphicImage.DynamicImageStore.ImageWrapper;

public class UIGraphicImage extends HtmlGraphicImage
{

   public static final String FAMILY = "org.jboss.seam.ui.UIGraphicImage";

   private String fileName;

   @Override
   public String getFamily()
   {
      return FAMILY;
   }

   @Override
   public Object saveState(FacesContext context)
   {
      Object[] values = new Object[2];
      values[0] = super.saveState(context);
      values[1] = fileName;
      return values;
   }

   @Override
   public void restoreState(FacesContext context, Object state)
   {
      Object values[] = (Object[]) state;
      super.restoreState(context, values[0]);
      fileName = (String) values[1];
   }

   @Override
   public void encodeBegin(FacesContext context) throws IOException
   {
      ResponseWriter writer = context.getResponseWriter();
      String key = getFileName();
      String extension = null;

      Image image = Image.instance();
      image.set(getValue());
      
      // Do transforms
      
      for (UIComponent cmp : (List<UIComponent>) this.getChildren()) 
      {
         if (cmp instanceof UIImageTransform)
         {
            UIImageTransform imageTransform = (UIImageTransform) cmp;
            Map<String, String> parameters = getParameters(imageTransform);
            if (UIImageTransform.SCALE.equals(imageTransform.getType()))
            {
               scale(imageTransform, image, parameters);
            } else if (UIImageTransform.TYPE.equals(imageTransform.getType()))
            {
               contentType(imageTransform, image, parameters);
            }
            
         }
      }

      key = DynamicImageStore.instance().put(new ImageWrapper(image.get(), image.getContentType()),
               key);
      extension = image.getContentType().getExtension();

      writer.startElement(HTML.IMG_ELEM, this);
      String url = context.getExternalContext().getRequestContextPath()
               + DynamicImageResource.DYNAMIC_IMAGE_RESOURCE_PATH + "/" + key + extension;
      writer.writeAttribute(HTML.SRC_ATTR, url, HTML.SRC_ATTR);
      HTML.renderHTMLAttributes(writer, this, HTML.IMG_PASSTHROUGH_ATTRIBUTES);
      writer.endElement(HTML.IMG_ELEM);
   }

   private void scale(UIImageTransform imageTransform, Image image, Map<String, String> parameters) throws NumberFormatException, IOException
   {
//    TODO reduce number of decimal places
      if ("true".equals(parameters.get("maintainRatio")))
      {
         if (parameters.containsKey("width") && parameters.containsKey("height"))
         {
            throw new UnsupportedOperationException("Cannot maintain ratio and specify height and width");
         }
         else if (parameters.containsKey("width"))
         {
            setHeight(((Double) (Double.valueOf(parameters.get("width")) / image.getRatio())).toString());
            setWidth(parameters.get("width"));
         }
         else if (parameters.containsKey("height"))
         {
            setWidth(((Double) (Double.valueOf(getHeight()) / image.getRatio())).toString());
            setHeight(parameters.get("height"));
         }
      }
   }
   
   private void contentType(UIImageTransform imageTransform, Image image, Map<String, String> parameters) {
      if (parameters.containsKey("output")) 
      {
        Image.Type type = Image.Type.getTypeByMimeType(parameters.get("output"));
        if (type != null)
        {
           image.setContentType(type);
        }
      }
      
   }
   
   private Map<String, String> getParameters(UIImageTransform imageTransform)
   {
      Map<String, String> parameters = new HashMap<String, String>();
      for (UIComponent cmp : (List<UIComponent>) imageTransform.getChildren())
      {
         if (cmp instanceof UIParameter)
         {
            UIParameter parameter = (UIParameter) cmp;
            parameters.put(parameter.getName(), parameter.getValue() == null ? null : parameter.getValue().toString());
         }
      }
      return parameters;
   }

   public String getFileName()
   {
      if (fileName != null)
      {
         return fileName;
      }
      else
      {
         ValueBinding vb = getValueBinding("fileName");
         return vb == null ? null : JSF.getStringValue(getFacesContext(), vb);
      }

   }

   public void setFileName(String fileName)
   {
      this.fileName = fileName;
   }

   @Override
   protected Renderer getRenderer(FacesContext context)
   {
      return null;
   }
   
   @Override
   public boolean getRendersChildren()
   {
      return true;
   }
}

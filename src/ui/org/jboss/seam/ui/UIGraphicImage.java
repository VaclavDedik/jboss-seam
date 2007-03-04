package org.jboss.seam.ui;

import java.io.IOException;

import javax.faces.component.html.HtmlGraphicImage;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.el.ValueBinding;
import javax.faces.render.Renderer;

import org.jboss.seam.core.Image;
import org.jboss.seam.ui.resource.DynamicImageResource;
import org.jboss.seam.ui.resource.DynamicImageStore;
import org.jboss.seam.ui.resource.DynamicImageStore.ImageWrapper;

public class UIGraphicImage extends HtmlGraphicImage
{

   public static final String FAMILY = "org.jboss.seam.ui.UIGraphicImage";

   private Boolean maintainRatio = true;

   private String contentType;
   
   private String fileName;

   @Override
   public String getFamily()
   {
      return FAMILY;
   }

   @Override
   public Object saveState(FacesContext context)
   {
      Object[] values = new Object[4];
      values[0] = super.saveState(context);
      values[1] = contentType;
      values[2] = maintainRatio;
      values[3] = fileName;
      return values;
   }

   @Override
   public void restoreState(FacesContext context, Object state)
   {
      Object values[] = (Object[]) state;
      super.restoreState(context, values[0]);
      maintainRatio = (Boolean) values[2];
      contentType = (String) values[1];
      fileName = (String) values[3];
   }

   public boolean isMaintainRatio()
   {
      if (contentType != null)
      {
         return maintainRatio;
      }
      else
      {
         ValueBinding vb = getValueBinding("maintainRatio");
         return vb == null ? false : JSF.getBooleanValue(getFacesContext(), vb);
      }
      
   }

   public void setMaintainRatio(boolean maintainRatio)
   {
      this.maintainRatio = maintainRatio;
   }

   @Override
   public void encodeBegin(FacesContext context) throws IOException
   {
      ResponseWriter writer = context.getResponseWriter();
      String key = getFileName();
      String extension = null;
      if (!DynamicImageStore.instance().contains(key)) 
      {
         Image image = Image.instance();
         image.set(getValue());
   
         if (isMaintainRatio() && getWidth() != null)
         {
            // TODO reduce number of decimal places
            setHeight(((Double) (Double.valueOf(getWidth()) / image.getRatio())).toString());
         }
         else if (isMaintainRatio() && getHeight() != null)
         {
            setWidth(((Double) (Double.valueOf(getHeight()) / image.getRatio())).toString());
         }
         
         if (getContentType() != null) {
            image.setContentType(Image.Type.getType(getContentType()));
         }
         
         key = DynamicImageStore.instance().put(new ImageWrapper(image.get(),
                  image.getContentType()), key);
         extension = image.getContentType().getExtension();
      } 
      else 
      {
         extension = DynamicImageStore.instance().get(key).getContentType().getExtension();
      }
      writer.startElement(HTML.IMG_ELEM, this);
      String url = context.getExternalContext().getRequestContextPath()
               + DynamicImageResource.DYNAMIC_IMAGE_RESOURCE_PATH + "/" + key + extension;
      writer.writeAttribute(HTML.SRC_ATTR, url, HTML.SRC_ATTR);
      HTML.renderHTMLAttributes(writer, this, HTML.IMG_PASSTHROUGH_ATTRIBUTES);
      writer.endElement(HTML.IMG_ELEM);
   }

   public String getContentType()
   {
      if (contentType != null)
      {
         return contentType;
      }
      else
      {
         ValueBinding vb = getValueBinding("contentType");
         return vb == null ? null : JSF.getStringValue(getFacesContext(), vb);
      }
   }

   public void setContentType(String contentType)
   {
      this.contentType = contentType;
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
}

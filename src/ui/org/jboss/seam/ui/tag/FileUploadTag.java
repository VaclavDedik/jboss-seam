package org.jboss.seam.ui.tag;

import javax.faces.component.UIComponent;

import org.jboss.seam.ui.FileUpload;

/**
 * File upload tag attributes
 * 
 * @author Shane Bryzak
 */
public class FileUploadTag extends UIComponentTagBase
{ 
   private String data;
   private String contentType;
   private String fileName;
   
   @Override
   public String getComponentType()
   {
       return FileUpload.COMPONENT_TYPE;
   }

   @Override
   public String getRendererType()
   {
       return null;
   }

   @Override
   protected void setProperties(UIComponent component)
   {
       super.setProperties(component);
   }
   
   public String getData()
   {
      return data;
   }
   
   public void setData(String data)
   {
      this.data = data;
   }
   
   public String getContentType()
   {
      return contentType;
   }
   
   public void setContentType(String contentType)
   {
      this.contentType = contentType;
   }
   
   public String getFileName()
   {
      return fileName;
   }
   
   public void setFileName(String fileName)
   {
      this.fileName = fileName;
   }
}

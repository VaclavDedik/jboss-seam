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
   private String fileSize;
   
   private String accept;
   private String required;
   private String disabled;
   private String styleClass;
   private String style;
   
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
   
   public String getAccept()
   {
      return accept;
   }

   public void setAccept(String accept)
   {
      this.accept = accept;
   }

   public String getDisabled()
   {
      return disabled;
   }

   public void setDisabled(String disabled)
   {
      this.disabled = disabled;
   }

   public String getRequired()
   {
      return required;
   }

   public void setRequired(String required)
   {
      this.required = required;
   }

   public String getStyle()
   {
      return style;
   }

   public void setStyle(String style)
   {
      this.style = style;
   }

   public String getStyleClass()
   {
      return styleClass;
   }

   public void setStyleClass(String styleClass)
   {
      this.styleClass = styleClass;
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
   
   public String getFileSize()
   {
      return fileSize;
   }
   
   public void setFileSize(String fileSize)
   {
      this.fileSize = fileSize;
   }
}

package org.jboss.seam.ui.tag;

import javax.faces.component.UIComponent;

import org.jboss.seam.ui.UIFileUpload;

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
   private String styleClass;
   private String style;
   
   @Override
   public String getComponentType()
   {
       return UIFileUpload.COMPONENT_TYPE;
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
       setStringProperty(component, "data", data);
       setStringProperty(component, "contentType", contentType);
       setStringProperty(component, "fileName", fileName);
       setStringProperty(component, "fileSize", fileSize);
       setStringProperty(component, "accept", accept);
       setStringProperty(component, "styleClass", styleClass);
       setStringProperty(component, "style", style);
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

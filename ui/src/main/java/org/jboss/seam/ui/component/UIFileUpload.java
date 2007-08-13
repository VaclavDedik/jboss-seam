package org.jboss.seam.ui.component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


import javax.el.ValueExpression;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

/**
 * JSF component class
 * 
 */
public abstract class UIFileUpload extends UIInput
{

   private String localContentType;

   private String localFileName;

   private Integer localFileSize;

   private InputStream localInputStream;

   @Override
   public void processUpdates(FacesContext context)
   {
      ValueExpression dataBinding = getValueExpression("data");
      if (dataBinding != null)
      {
         Class clazz = dataBinding.getType(context.getELContext());
         if (clazz.isAssignableFrom(InputStream.class))
         {
            dataBinding.setValue(context.getELContext(), getLocalInputStream());
         }
         else if (clazz.isAssignableFrom(byte[].class))
         {
            byte[] bytes = null;
            if (getLocalInputStream() != null)
            {
               ByteArrayOutputStream bos = new ByteArrayOutputStream();
               try
               {                  
                  byte[] buffer = new byte[512];
                  int read = getLocalInputStream().read(buffer);
                  while (read != -1)
                  {
                     bos.write(buffer, 0, read);
                     read = getLocalInputStream().read(buffer);
                  }
                  bytes = bos.toByteArray();              
               }
               catch (IOException e)
               {
                  throw new RuntimeException(e);
               }
            }
            dataBinding.setValue(context.getELContext(), bytes);
         }
         
         if (getLocalContentType() != null)
         {
            ValueExpression valueExpression = getValueExpression("contentType");
            if (valueExpression != null) 
            {
               valueExpression.setValue(context.getELContext(), getLocalContentType());
            }
         }

         if (getLocalFileName() != null)
         {
            ValueExpression valueExpression = getValueExpression("fileName");
            if (valueExpression != null)
            {
               valueExpression.setValue(context.getELContext(), getLocalFileName());
            }
         }

         if (getLocalFileSize() != null)
         {
            ValueExpression valueExpression = getValueExpression("fileSize");
            if (valueExpression != null)
            {
               valueExpression.setValue(context.getELContext(), getLocalFileSize());
            }
         }
      }    
   }

   public String getLocalContentType()
   {
      return localContentType;
   }

   public void setLocalContentType(String localContentType)
   {
      this.localContentType = localContentType;
   }

   public String getLocalFileName()
   {
      return localFileName;
   }

   public void setLocalFileName(String localFileName)
   {
      this.localFileName = localFileName;
   }

   public Integer getLocalFileSize()
   {
      return localFileSize;
   }

   public void setLocalFileSize(Integer localFileSize)
   {
      this.localFileSize = localFileSize;
   }

   public InputStream getLocalInputStream()
   {
      return localInputStream;
   }

   public void setLocalInputStream(InputStream localInputStream)
   {
      this.localInputStream = localInputStream;
   }
   
   public abstract void setAccept(String accept);
   
   public abstract String getAccept();
   
   public abstract String getStyleClass();

   public abstract String getStyle();
   
   public abstract void setStyleClass(String styleClass);
   
   public abstract void setStyle(String style);

}

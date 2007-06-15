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

   private int localFileSize;

   private InputStream localInputStream;

   private static final String COMPONENT_TYPE = "org.jboss.seam.ui.FileUpload";

   private static final String COMPONENT_FAMILY = "org.jboss.seam.ui.FileUpload";

   @Override
   public void processUpdates(FacesContext context)
   {

      ValueExpression dataBinding = getValueExpression("data");
      if (dataBinding != null)
      {
         Class cls = dataBinding.getType(context.getELContext());
         if (cls.isAssignableFrom(InputStream.class))
         {
            dataBinding.setValue(context.getELContext(), localInputStream);
         }
         else if (cls.isAssignableFrom(byte[].class))
         {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try
            {
               while (localInputStream.available() > 0)
               {
                  bos.write(localInputStream.read());
                  dataBinding.setValue(context.getELContext(), bos.toByteArray());
               }
            }
            catch (IOException e)
            {
               throw new RuntimeException(e);
            }
         }

         ValueExpression vb = getValueExpression("contentType");
         if (vb != null) vb.setValue(context.getELContext(), localContentType);

         vb = getValueExpression("fileName");
         if (vb != null) vb.setValue(context.getELContext(), localFileName);

         vb = getValueExpression("fileSize");
         if (vb != null) vb.setValue(context.getELContext(), localFileSize);
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

   public int getLocalFileSize()
   {
      return localFileSize;
   }

   public void setLocalFileSize(int localFileSize)
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
   
   

}

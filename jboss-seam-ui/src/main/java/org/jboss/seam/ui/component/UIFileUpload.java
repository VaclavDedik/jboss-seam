package org.jboss.seam.ui.component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.el.ValueExpression;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import org.richfaces.cdk.annotations.Attribute;
import org.richfaces.cdk.annotations.Description;
import org.richfaces.cdk.annotations.JsfComponent;
import org.richfaces.cdk.annotations.JsfRenderer;
import org.richfaces.cdk.annotations.Tag;

/**
 * JSF component class which renders a file upload control. 
 * This control must be used within a form with an encoding type of multipart/form-data
 * 
 */
@JsfComponent(description=@Description(displayName="org.jboss.seam.ui.FileUpload",value="Renders a file upload control. This control must be used within a form with an encoding type of multipart/form-data"),
family="org.jboss.seam.ui.FileUpload", type="org.jboss.seam.ui.FileUpload",generate="org.jboss.seam.ui.component.html.HtmlFileUpload", 
tag = @Tag(baseClass="org.jboss.seam.ui.util.cdk.UIComponentTagBase", name="fileUpload"),
renderer = @JsfRenderer(type="org.jboss.seam.ui.FileUploadRenderer", family="org.jboss.seam.ui.FileUploadRenderer"),
attributes = {"core-props.xml", "focus-props.xml", "javax.faces.component.EditableValueHolder.xml" })
public abstract class UIFileUpload extends UIInput
{

   private String localContentType;

   private String localFileName;

   private Integer localFileSize;

   private InputStream localInputStream;

   @Override
   public void processUpdates(FacesContext context)
   {
      
      // Skip processing if rendered flag is false.
      // this logic is in javax.faces.component.UIInput.processUpdates(FacesContext context) 
      if (!isRendered()) {
         return;
      }
      
      ValueExpression dataBinding = getValueExpression("data");
      if (dataBinding != null)
      {
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
      this.localFileName = extractFilename(localFileName);
   }
   
   /**
    * Workaround for IE, which includes the full path to the file.
    */
   private String extractFilename(String filename)
   {
      if (filename != null && filename.lastIndexOf("\\") > -1)
      {
         return filename.substring(filename.lastIndexOf("\\") + 1);
      }
      else
      {
         return filename;
      }
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
   
   /**
    * {@inheritDoc}
    *
    * @see javax.faces.component.UIOutput#getLocalValue()
    */
   @Override
   public Object getLocalValue() {
       return new LocalUploadValue(localContentType, localFileName, localFileSize,
               localInputStream);
   }

   /**
    * {@inheritDoc}
    *
    * @see javax.faces.component.UIInput#setValue(java.lang.Object)
    */
   @Override
   public void setValue(Object value) {
       // Check if the local values get restored
       if (value != null && value instanceof LocalUploadValue) {
           LocalUploadValue localValue = (LocalUploadValue) value;
           localFileName = localValue.getFileName();
           localFileSize = localValue.getFileSize();
           localContentType = localValue.getContentType();
           localInputStream = localValue.getInputStream();
       } else {
           super.setValue(value);
       }
   }

   /**
    * {@inheritDoc}
    *
    * @see javax.faces.component.UIInput#isLocalValueSet()
    */
   @Override
   public boolean isLocalValueSet() {
       return localContentType != null || localFileName != null || localFileSize != null
               || localInputStream != null;
   }

   /**
    * Helper class to store the local values.
    */
   protected class LocalUploadValue {

       /** Stores the local content type. */
       private String contentType;

       /** Stores the local file name. */
       private String fileName;

       /** Stores the local file size. */
       private Integer fileSize;

       /** Stores the local stream information. */
       private InputStream inputStream;

       /**
        * Constructor for this class.
        *
        * @param contentType
        * The local content type to save
        * @param fileName
        * The local file name to save
        * @param fileSize
        * The local file size to save
        * @param inputStream
        * The local input stream to save
        */
       public LocalUploadValue(String contentType, String fileName, Integer fileSize,
               InputStream inputStream) {
           super();
           this.contentType = contentType;
           this.fileName = fileName;
           this.fileSize = fileSize;
           this.inputStream = inputStream;
       }

       /**
        * Returns the contentType value.
        *
        * @return the contentType value
        */
       public String getContentType() {
           return contentType;
       }

       /**
        * Returns the fileName value.
        *
        * @return the fileName value
        */
       public String getFileName() {
           return fileName;
       }

       /**
        * Returns the fileSize value.
        *
        * @return the fileSize value
        */
       public Integer getFileSize() {
           return fileSize;
       }

       /**
        * Returns the inputStream value.
        *
        * @return the inputStream value
        */
       public InputStream getInputStream() {
           return inputStream;
       }
   }
   
   public abstract void setAccept(String accept);
   
   @Attribute(description = @Description("a comma-separated list of content types to accept, " +
           "may not be supported by the browser. E.g. \"images/png,images/jpg\",\"images/*\"."))
   public abstract String getAccept();

   @Attribute(description = @Description("this value binding receives the file's content type (optional)."))
   public abstract Object getData();

   @Attribute(description = @Description("the property to receive the contentType"))
   public abstract String getContentType();

   @Attribute(description = @Description("this value binding receives the filename (optional)."))
   public abstract String getFileName();

   @Attribute(description = @Description("this value binding receives the file size (optional)."))
   public abstract Integer getFileSize();
   
   @Attribute
   public abstract String getStyleClass();

   @Attribute
   public abstract String getStyle();
   
   public abstract void setStyleClass(String styleClass);
   
   public abstract void setStyle(String style);

}

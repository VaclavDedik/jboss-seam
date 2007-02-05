package org.jboss.seam.mail.ui;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.URLDataSource;
import javax.faces.FacesException;
import javax.faces.component.ValueHolder;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;

import org.jboss.seam.pdf.DocumentData;
import org.jboss.seam.pdf.ui.UIDocument;
import org.jboss.seam.ui.JSF;
import org.jboss.seam.util.Resources;

public class UIAttachment extends MailComponent implements ValueHolder
{

   private Object value;

   private String contentType;

   private String fileName;

   public Object getValue()
   {
      if (value != null)
      {
         return value;
      }
      else
      {
         return getValue("value");
      }
   }

   public void setValue(Object value)
   {
      this.value = value;
   }
   
   @Override
   public void encodeBegin(FacesContext context) throws IOException
   {
      if (this.getChildCount() > 0) {
         if (this.getChildren().get(0) instanceof UIDocument) {
            UIDocument document = (UIDocument) this.getChildren().get(0);
            document.setSendRedirect(false);
            JSF.renderChildren(context, this);
         } else {
            setValue(encode(context).getBytes());
            if (getContentType() == null) {
               // User hasn't specified content, assume html
               setContentType("text/html");
            }
         }
      }
   }

   @Override
   public void encodeEnd(FacesContext context) throws IOException
   {
      DataSource ds = null;
      try
      {
         if (getValue() instanceof URL)
         {
            URL url = (URL) getValue();
            ds = new URLDataSource(url);
         }
         else if (getValue() instanceof File)
         {
            File file = (File) getValue();
            ds = new FileDataSource(file);
         }
         else if (getValue() instanceof String)
         {
            String string = (String) getValue();
            ds = new URLDataSource(Resources.getResource(string));
         }
         else if (getValue() instanceof InputStream)
         {
            InputStream is = (InputStream) getValue();
            ds = new ByteArrayDataSource(is, getContentType());
         }
         else if (getValue() instanceof DocumentData)
         {
            DocumentData documentData = (DocumentData) getValue();
            ds = new ByteArrayDataSource(documentData.getData(), documentData.getDocType().getMimeType());
         }
         else if (getValue() != null && getValue().getClass().isArray())
         {
            if (getValue().getClass().getComponentType().isAssignableFrom(Byte.TYPE))
            {
               byte[] b = (byte[]) getValue();
               ds = new ByteArrayDataSource(b, getContentType());
            }
         }
         if (ds != null)
         {
            BodyPart attachment = new MimeBodyPart();
            attachment.setDataHandler(new DataHandler(ds));
            attachment.setFileName(getName(ds.getName()));
            super.getRootMultipart().addBodyPart(attachment);
         }
      }
      catch (MessagingException e)
      {
         throw new FacesException(e.getMessage(), e);
      }
   }

   public String getContentType()
   {
      if (contentType == null)
      {
         return getString("contentType");
      }
      else
      {
         return contentType;
      }
   }

   public void setContentType(String contentType)
   {
      this.contentType = contentType;
   }

   public String getFileName()
   {
      if (fileName == null)
      {
         return getString("fileName");
      }
      else
      {
         return fileName;
      }
   }

   public void setFileName(String fileName)
   {
      this.fileName = fileName;
   }

   private String removePath(String fileName)
   {
      if (fileName.lastIndexOf("/") > 0)
      {
         return fileName.substring(fileName.lastIndexOf("/") + 1);
      }
      else
      {
         return fileName;
      }
   }

   private String getName(String name)
   {
      if (getFileName() != null)
      {
         return getFileName();
      }
      else
      {
         return removePath(name);
      }
   }

   public Converter getConverter()
   {
      return null;
   }

   public Object getLocalValue()
   {
      return value;
   }

   public void setConverter(Converter converter)
   {
      throw new UnsupportedOperationException("Cannot attach a converter to an attachment");
   }

}

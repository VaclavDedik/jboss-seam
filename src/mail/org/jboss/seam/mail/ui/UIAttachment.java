package org.jboss.seam.mail.ui;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.URLDataSource;
import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;

import org.jboss.seam.util.Resources;

public class UIAttachment extends MailComponent
{

   private Object value;

   private String contentType;

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
      DataSource ds = null;
      // TODO Support seam-pdf
      // TODO Support byte array, input stream
      // TODO Override content type, file name
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
         if (ds != null)
         {
            BodyPart attachment = new MimeBodyPart();
            attachment.setDataHandler(new DataHandler(ds));
            // TODO Make this default to just the filename
            attachment.setFileName(ds.getName());
            super.getRootMultipart().addBodyPart(attachment);
         }
      }
      catch (MessagingException e)
      {
         throw new FacesException(e.getMessage(), e);
      }
   }

   @Override
   public void encodeChildren(FacesContext context) throws IOException
   {
      // No children
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

}

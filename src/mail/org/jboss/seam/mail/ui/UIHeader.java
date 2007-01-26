package org.jboss.seam.mail.ui;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.mail.MessagingException;

public class UIHeader extends MailComponent
{
   
   private String name;
   private String value;
   
   @Override
   public void encodeBegin(FacesContext facesContext) throws IOException
   {
      try
      {
         if (getValue() != null) {
            findMimeMessage().addHeader(getName(), getValue());
         } else {
            findMimeMessage().addHeader(getName(), encode(facesContext));
         }
      }
      catch (MessagingException e)
      {
         throw new FacesException(e.getMessage(), e);
      }
   }
   
   
   public String getName()
   {
      if (name == null)
      {
         return getString("header");
      }
      else 
      {
         return name;
      }
   }
   
   public void setName(String header)
   {
      this.name = header;
   }
   
   public String getValue()
   {
      if (value == null)
      {
         return getString("value");
      }
      else 
      {
         return value;
      }
   }
   
   public void setValue(String value)
   {
      this.value = value;
   }
}

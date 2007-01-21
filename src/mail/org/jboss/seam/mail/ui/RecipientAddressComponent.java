package org.jboss.seam.mail.ui;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.mail.Message.RecipientType;
import javax.mail.internet.MimeMessage;

/**
 * Encode a recipient.  Work is done here, subclasses simply need to
 * specify a RecipientType 
 */
public abstract class RecipientAddressComponent extends AddressComponent
{
   
   @Override
   public void encodeBegin(FacesContext facesContext) throws IOException
   {
      try
      {
         MimeMessage mimeMessage = findMimeMessage();
         mimeMessage.addRecipient(getRecipientType(), getInternetAddress(facesContext));
      }
      catch (Exception e)
      {
         throw new FacesException(e);
      }
   }
  
   protected abstract RecipientType getRecipientType();
   
   

}

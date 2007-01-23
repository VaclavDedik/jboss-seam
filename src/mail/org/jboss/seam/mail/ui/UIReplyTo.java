package org.jboss.seam.mail.ui;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.mail.Address;
import javax.mail.internet.MimeMessage;

/**
 * JSF component for rendering a Reply-to header
 */
public class UIReplyTo extends AddressComponent
{
   
   @Override
   public void encodeBegin(FacesContext facesContext) throws IOException
   {
      try
      {
         MimeMessage mimeMessage = findMimeMessage();
         if (mimeMessage.getReplyTo() != null && mimeMessage.getReplyTo().length > 0) {
            throw new UnsupportedOperationException("Email cannot have more than one from address");
         }
         Address[] replyTo = {getInternetAddress(facesContext)}; 
         mimeMessage.setReplyTo(replyTo);
      }
      catch (Exception e)
      {
        throw new FacesException(e);
      }
   }
}

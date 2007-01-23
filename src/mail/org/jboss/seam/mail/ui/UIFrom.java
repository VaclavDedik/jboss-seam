package org.jboss.seam.mail.ui;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.mail.internet.MimeMessage;

/**
 * JSF Component for rendering a from address
 */
public class UIFrom extends AddressComponent
{
   
   @Override
   public void encodeBegin(FacesContext facesContext) throws IOException
   {
      try
      {
         
         MimeMessage mimeMessage = findMimeMessage();
        if (mimeMessage.getFrom() != null && mimeMessage.getFrom().length > 0) {
           throw new UnsupportedOperationException("Email cannot have more than one from address");
        }
         mimeMessage.setFrom(getInternetAddress(facesContext));
      }
      catch (Exception e)
      {
        throw new FacesException(e);
      }
   }
}

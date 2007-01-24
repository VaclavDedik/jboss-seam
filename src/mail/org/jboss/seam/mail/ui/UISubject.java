package org.jboss.seam.mail.ui;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.mail.MessagingException;

/**
 * JSF component for rendering subject line
 */
public class UISubject extends MailComponent
{
   @Override
   public void encodeChildren(FacesContext facesContext) throws IOException
   {
      try
      {
         String subject = encode(facesContext);
         findMimeMessage().setSubject(subject);
      }
      catch (MessagingException e)
      {
         throw new FacesException(e.getMessage(), e);
      }
   }
}

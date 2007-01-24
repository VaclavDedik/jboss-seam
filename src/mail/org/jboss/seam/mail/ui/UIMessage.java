package org.jboss.seam.mail.ui;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import org.jboss.seam.mail.MailSession;

/**
 * JSF component which delimites the start and end of the mail message.  
 */
// TODO Support Priority,read receipt, encoding, precedence
// TODO Support getting session from JNDI
public class UIMessage extends MailComponent
{
   private MimeMessage mimeMessage;
   private Session session;

   /**
    * Get the JavaMail Session to use.
    * If not set the default session is used
    */
   public Session getMailSession()
   {
      if (session == null) {
         if (getValue("session") !=  null) {
            session = (Session) getValue("session");
         } else {
            session = MailSession.instance();
         }
      }
      return session;
   }

   public void setMailSession(Session session)
   {
      this.session = session;
   }
   
   public MimeMessage getMimeMessage() {
      if (mimeMessage == null) {
         mimeMessage = new MimeMessage(getMailSession());
      }
      return mimeMessage;
   }
   
   @Override
   public void encodeEnd(FacesContext arg0) throws IOException
   {
      super.encodeEnd(arg0);
      try
      {
         Transport.send(getMimeMessage());
      }
      catch (MessagingException e)
      {
         throw new FacesException(e.getMessage(), e);
      }
   }
   
   @Override
   public boolean getRendersChildren()
   {
    return false;
   }

}

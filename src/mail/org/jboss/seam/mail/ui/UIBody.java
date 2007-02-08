package org.jboss.seam.mail.ui;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * JSF component for rendering the body
 * Supports plain text, html bodies and setting an alternative
 * (text) part using an alternative facet
 *
 */
public class UIBody extends MailComponent
{
   
   public static final String HTML="html";
   public static final String PLAIN = "plain";
   
   private String type = HTML;
   
   @Override
   public void encodeChildren(FacesContext facesContext) throws IOException
   {
     try
     {
        String body = encode(facesContext);
        
        BodyPart bodyPart = new MimeBodyPart();
        if (PLAIN.equalsIgnoreCase(type)) 
        {
          bodyPart.setText(body);
        }
        else if (HTML.equals(type)) 
        {
           UIComponent alternative = getFacet("alternative");
           if (alternative != null)
           {
              BodyPart text = new MimeBodyPart();
              text.setText(encode(facesContext,alternative));
              text.addHeader("Content-Disposition", "inline");
              BodyPart html = new MimeBodyPart();
              html.setContent(body, "text/html");
              text.addHeader("Content-Disposition", "inline");
              Multipart multipart = new MimeMultipart("alternative");
              multipart.addBodyPart(text);
              multipart.addBodyPart(html);
              bodyPart.setContent(multipart);
              
           }
           else
           {   
              bodyPart.setContent(body, "text/html");
           }
        }
        getRootMultipart().addBodyPart(bodyPart);
      }
      catch (MessagingException e)
      {
         throw new FacesException(e.getMessage(), e);
      }
   }
   
   public void setType(String type)
   {
      this.type = type;
   }
   
   /**
    * The type of the body - plain or html
    */
   public String getType()
   {
      if (type == null) 
      {
         return getString("type");
      }
      return type;
   }

}

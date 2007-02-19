package org.jboss.seam.mail.ui;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

/**
 * JSF component for rendering the body Supports plain text, html bodies and
 * setting an alternative (text) part using an alternative facet
 * 
 */
public class UIBody extends MailComponent
{

   public static final String HTML = "html";

   public static final String PLAIN = "plain";

   private String type = HTML;

   @Override
   public void encodeChildren(FacesContext facesContext) throws IOException
   {
      try
      {
         String body = encode(facesContext);
         BodyPart bodyPart = null;
         if (PLAIN.equalsIgnoreCase(type))
         {
            bodyPart = getTextBody(facesContext, body);
         }
         else if (HTML.equals(type))
         {
            UIComponent alternative = getFacet("alternative");
            if (alternative != null)
            {
               Multipart multipart = new MimeMultipart("alternative");

               multipart.addBodyPart(getTextBody(facesContext, encode(facesContext,
                        alternative)));
               multipart.addBodyPart(getHtmlBody(facesContext, body));

               bodyPart = new MimeBodyPart();
               bodyPart.setContent(multipart);

            }
            else
            {
               bodyPart = getHtmlBody(facesContext, body);
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

   private BodyPart getTextBody(FacesContext facesContext, Object body)
            throws MessagingException
   {
      BodyPart bodyPart = new MimeBodyPart();
      bodyPart.setDisposition("inline");
      if ( facesContext.getResponseWriter().getCharacterEncoding() != null) 
      {
         bodyPart.setContent(body, "text/plain; charset="
               + facesContext.getResponseWriter().getCharacterEncoding() + "; format=flowed");
      } 
      else 
      {
         bodyPart.setContent(body, "text/plain");
      }
      return bodyPart;
   }

   private BodyPart getHtmlBody(FacesContext facesContext, Object body)
            throws MessagingException
   {
      BodyPart bodyPart = new MimeBodyPart();
      bodyPart.setDisposition("inline");
      if ( facesContext.getResponseWriter().getCharacterEncoding() != null) 
      {
         bodyPart.setContent(body, "text/html; charset="
                  + facesContext.getResponseWriter().getCharacterEncoding());
      } 
      else 
      {
         bodyPart.setContent(body, "text/html");
      }
      
      return bodyPart;
   }

}
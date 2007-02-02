package org.jboss.seam.example.mail;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.mail.MessagingException;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.core.Renderer;
import org.jboss.seam.log.Log;

@Name("mailExample")
public class MailExample
{
   
   @Logger
   private Log log;
   
   @In(create=true)
   private FacesMessages facesMessages;
   
   @In(create=true)
   private Renderer renderer;
   
   public void send() {
      try {
        renderer.render("/simple.xhtml");
        facesMessages.add("Email sent successfully");
      } catch (Exception e) {
         log.debug("Error send mail", e);
         facesMessages.add(FacesMessage.SEVERITY_INFO, "Email sending failed: " + e.getMessage());
      }
   }
   
   public void sendHtml() {
      try {
         renderer.render("/html.xhtml");
         facesMessages.add("Email sent successfully");
      } catch (Exception e) {
         facesMessages.add("Email sending failed:" + e.getMessage());
      }
    }

   public void sendPlain() {
      try {
         renderer.render("/plain.xhtml");
         facesMessages.add("Email sent successfully");
      } catch (Exception e) {
         facesMessages.add("Email sending failed: " + e.getMessage());
      }
    }
   
}

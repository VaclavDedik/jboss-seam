package org.jboss.seam.example.mail;


import java.net.URL;
import java.util.List;

import javax.faces.application.FacesMessage;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.core.Renderer;
import org.jboss.seam.log.Log;
import org.jboss.seam.util.Resources;

@Name("mailExample")
@Scope(ScopeType.CONVERSATION)
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
         log.error("Error sending mail", e);
         facesMessages.add(FacesMessage.SEVERITY_INFO, "Email sending failed: " + e.getMessage());
      }
   }
   
   public void sendAttachment() {
      try {
        renderer.render("/attachment.xhtml");
        facesMessages.add("Email sent successfully");
      } catch (Exception e) {
         log.error("Error sending mail", e);
         facesMessages.add(FacesMessage.SEVERITY_INFO, "Email sending failed: " + e.getMessage());
      }
   }
   
   public void sendHtml() {
      try {
         renderer.render("/html.xhtml");
         facesMessages.add("Email sent successfully");
      } catch (Exception e) {
         log.error("Error sending mail", e);
         facesMessages.add("Email sending failed:" + e.getMessage());
      }
    }

   public void sendPlain() {
      try {
         renderer.render("/plain.xhtml");
         facesMessages.add("Email sent successfully");
      } catch (Exception e) {
         log.error("Error sending mail", e);
         facesMessages.add("Email sending failed: " + e.getMessage());
      }
    }
   
   @Factory("numbers")
   public URL getFile()
   {
      return Resources.getResource("/numbers.csv");
   }
   
}

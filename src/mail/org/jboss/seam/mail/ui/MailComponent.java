package org.jboss.seam.mail.ui;

import java.io.IOException;
import java.io.StringWriter;

import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.jboss.seam.ui.JSF;

/**
 * Abstract base class for mail ui components
 *
 */
public abstract class MailComponent extends UIComponentBase
{

   private static final String FAMILY = "org.jboss.seam.mail";

   @Override
   public String getFamily()
   {
     return FAMILY;
   }
   
   @Override
   public boolean getRendersChildren() {
       return true;
   }
   
   
   protected String encode(FacesContext facesContext) throws IOException {
     return encode(facesContext, this);
   }
   
   /**
    * Encode the children of cmp, writing to a string (rather than the http response object)
    * and return the string
    */
   protected String encode(FacesContext facesContext,UIComponent cmp) throws IOException {
      ResponseWriter response = facesContext.getResponseWriter();
      StringWriter stringWriter = new StringWriter();
      ResponseWriter cachingResponseWriter = response.cloneWithWriter(stringWriter);
      facesContext.setResponseWriter(cachingResponseWriter);
      JSF.renderChildren(facesContext, cmp);
      facesContext.setResponseWriter(response);
      String output = stringWriter.getBuffer().toString();
      return output;
   }
   
   /**
    * look up the tree for mail message
    */
   public MimeMessage findMimeMessage() throws MessagingException {
       UIMessage parent = (UIMessage) findParent(this, UIMessage.class);
       if (parent != null) 
       {
           return parent.getMimeMessage();
       }
       else 
       {
           return null;
       }
   }
   
   public MimeMultipart getRootMultipart() throws IOException, MessagingException {
      return (MimeMultipart) findMimeMessage().getContent();
   }
   
   public MailComponent findParent(UIComponent parent) {
      return findParent(parent, null);
   }

   /**
    * find the first parent that is a mail component of a given type
    */
   public MailComponent findParent(UIComponent parent, Class<?> c) {
       if (parent == null) 
       {
           return null;
       }
       
       if (parent instanceof MailComponent) 
       {
           if (c==null || c.isAssignableFrom(parent.getClass())) 
           {
               return (MailComponent) parent;
           }
       }

       return findParent(parent.getParent(),c);
   }
   
   /**
    * Get a valuebinding as a string
    */
   protected String getString(String localName) {
      if (getValue(localName) != null) {
         return getValue(localName).toString();
      } else {
         return null;
      }
   }
   
   /**
    * Get a vauebinding
    */
   protected Object getValue(String localName) {
      if (getValueBinding(localName) == null) {
         return null;
      } else {
         return getValueBinding(localName).getValue(getFacesContext());
      }
   }
}

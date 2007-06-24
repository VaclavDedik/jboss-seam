package org.jboss.seam.webservice;

import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.contexts.ServletLifecycle;

/**
 * 
 * @author Shane Bryzak
 */
public class SOAPRequestHandler implements SOAPHandler
{
   public static final String MESSAGE_CONTEXT = "org.jboss.seam.ws.messageContext";
   public static final QName CIDQN = new QName("http://www.jboss.org/seam/ws", "conversationId", "seam");   
   
   private Set<QName> headers = new HashSet<QName>();
   
   private String handlerName;

   public boolean handleInbound(MessageContext messageContext)
   {
      HttpServletRequest request = (HttpServletRequest) messageContext.get(MessageContext.SERVLET_REQUEST);      
      ServletLifecycle.beginRequest(request);
      Contexts.getEventContext().set(MESSAGE_CONTEXT, messageContext);
      
      String conversationId = null; //extractConversationId()
      
      // put the clientid in the message context
      messageContext.put("conversationId", conversationId);

      return true;
   }

   public boolean handleOutbound(MessageContext msgContext)
   {
      try
      {                
         SOAPMessageContext smc = (SOAPMessageContext) msgContext;
         
         SOAPElement element = smc.getMessage().getSOAPHeader().addChildElement(CIDQN);
         element.addTextNode(msgContext.get("org.jboss.seam.conversationId").toString());
         
         smc.getMessage().saveChanges();
      }
      catch (SOAPException ex)
      {
         throw new IllegalStateException("Cannot handle response", ex);
      }

      return true;
   }

   
   public void close(MessageContext messageContext)
   {     
      Lifecycle.endRequest();
   }
   
   public Set<QName> getHeaders()
   {
      return headers;
   }

   public void setHeaders(Set<QName> headers)
   {
      this.headers = headers;
   }   
   
   public String getHandlerName()
   {
      return handlerName;
   }

   public void setHandlerName(String handlerName)
   {
      this.handlerName = handlerName;
   }

   public boolean handleMessage(MessageContext msgContext)
   {
      Boolean outbound = (Boolean)msgContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
      if (outbound == null)
         throw new IllegalStateException("Cannot obtain required property: " + MessageContext.MESSAGE_OUTBOUND_PROPERTY);

      return outbound ? handleOutbound(msgContext) : handleInbound(msgContext);
   }

   public boolean handleFault(MessageContext messagecontext)
   {
      return true;
   }
   
   @Override
   public String toString()
   {
      return (handlerName != null ? handlerName : super.toString());
   }      
}

package org.jboss.seam.webservice;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.contexts.ServletLifecycle;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

/**
 * A SOAP request handler for controling Seam's lifecycle and managing
 * conversation propagation.
 * 
 * @author Shane Bryzak
 */
public class SOAPRequestHandler implements SOAPHandler
{
   /**
    * The MessageContext is stored in event scope under this key
    */
   public static final String MESSAGE_CONTEXT = "org.jboss.seam.ws.messageContext";
   
   /**
    * The QName for the conversation ID element
    */
   public static final QName CIDQN = new QName("http://www.jboss.org/seam/ws", "conversationId", "seam");
   
   private static final LogProvider log = Logging.getLogProvider(SOAPRequestHandler.class);   
   
   private Set<QName> headers = new HashSet<QName>();
   
   private String handlerName;
   
   /**
    * Handle inbound and outbound messages
    * 
    * @param msgContext The message context
    * @return boolean true if processing should continue
    */
   public boolean handleMessage(MessageContext msgContext)
   {
      Boolean outbound = (Boolean)msgContext.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
      if (outbound == null)
         throw new IllegalStateException("Cannot obtain required property: " + MessageContext.MESSAGE_OUTBOUND_PROPERTY);

      return outbound ? handleOutbound(msgContext) : handleInbound(msgContext);
   }   

   /**
    * Inbound message handler. Seam contexts should be initialized here, and
    * the conversation ID (if present) is extracted from the request.
    * 
    * @param messageContext The message context
    * @return boolean true if processing should continue
    */
   public boolean handleInbound(MessageContext messageContext)
   {
      try
      {
         HttpServletRequest request = (HttpServletRequest) messageContext.get(MessageContext.SERVLET_REQUEST);      
         ServletLifecycle.beginRequest(request);
         Contexts.getEventContext().set(MESSAGE_CONTEXT, messageContext);
         
         String conversationId = extractConversationId(messageContext);
         
         // put the clientid in the message context
         messageContext.put("conversationId", conversationId);
   
         return true;
      }
      catch (SOAPException ex)
      {
         log.error("Error handling inbound SOAP request", ex);
         return false;
      }
   }
   
   private String extractConversationId(MessageContext msgContext)
      throws SOAPException
   {
      SOAPMessageContext smc = (SOAPMessageContext) msgContext;
      SOAPHeader header = smc.getMessage().getSOAPHeader();
      
      Iterator iter = header.getChildElements(CIDQN);
      if (iter.hasNext())
      {
         SOAPElement element = (SOAPElement) iter.next();
         return element.getTextContent();
      }
      else
      {
         return null;
      }
   }

   /**
    * Our outbound message handler.  This is where we set the outbound conversation ID
    * 
    * @param msgContext The message context
    * @return boolean true if processing should continue
    */
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

   /**
    * Called just prior to dispatching a message, fault or exception. The 
    * Seam request lifecycle is ended here
    */
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

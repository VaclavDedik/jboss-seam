package org.jboss.seam.ws;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.Manager;

/**
 * A SOAP request handler that instantiates the Seam contexts for a web service
 * request.
 *  
 * @author Shane Bryzak
 */
public class SeamWSRequestHandler implements SOAPHandler
{
   public static final String MESSAGE_CONTEXT = "org.jboss.seam.ws.messageContext";
   
   public Set getHeaders()
   {
      return null;
   }

   public void close(MessageContext messageContext)
   {     
      Lifecycle.endRequest();
   }

   public boolean handleFault(MessageContext messageContext)
   {
      return true;
   }

   public boolean handleMessage(MessageContext messageContext)
   {  
      HttpServletRequest request = (HttpServletRequest) messageContext.get(MessageContext.SERVLET_REQUEST);      
      Lifecycle.beginRequest(Lifecycle.getServletContext(), request.getSession(), request);
      
      Contexts.getEventContext().set(MESSAGE_CONTEXT, messageContext);
      
      return true;
   }

}

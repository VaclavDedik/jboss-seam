package org.jboss.seam.interceptors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.handler.MessageContext;

import org.jboss.seam.annotations.AroundInvoke;
import org.jboss.seam.annotations.Conversation;
import org.jboss.seam.annotations.ConversationId;
import org.jboss.seam.annotations.Interceptor;
import org.jboss.seam.contexts.ContextAdaptor;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.core.Manager;
import org.jboss.seam.intercept.InvocationContext;
import org.jboss.seam.ws.SeamWSRequestHandler;

/**
 * Implements conversation management for web services.
 * 
 * @author Shane Bryzak
 */
@Interceptor(stateless=true,
         around={ValidationInterceptor.class, BijectionInterceptor.class},
         within=BusinessProcessInterceptor.class)
public class WebServiceInterceptor extends AbstractInterceptor
{
   @AroundInvoke
   public Object aroundInvoke(InvocationContext invocation) throws Exception
   {
      try
      {
         MessageContext messageContext = (MessageContext) Contexts.getEventContext().get(
                  SeamWSRequestHandler.MESSAGE_CONTEXT);
         HttpServletRequest request = (HttpServletRequest) messageContext.get(
                  MessageContext.SERVLET_REQUEST);
         
         String conversationId = extractConversationId(invocation);
         
         Manager.instance().restoreConversation(conversationId);
         Lifecycle.resumeConversation(request.getSession());    
         
         Object result = invocation.proceed();
         
         Manager.instance().endRequest( ContextAdaptor.getSession(request.getSession()) );
         Lifecycle.endRequest();        
         
         return result;
      }
      finally
      {
         Lifecycle.setServletRequest(null);
         Lifecycle.setPhaseId(null);         
      } 
      
   }
   
   private String extractConversationId(InvocationContext invocation)
   {
      Method method = invocation.getMethod();
      
      for ( int i = 0; i < method.getParameterAnnotations().length; i++)
      {         
         Annotation[] annotations = method.getParameterAnnotations()[i];
         
         for ( Annotation annotation : annotations )
         {
            if (annotation.annotationType().equals(ConversationId.class))
            {
               Conversation conversation = method.getDeclaringClass().getAnnotation(Conversation.class);
               String conversationName = conversation != null ?
                        conversation.value() : null;
               
               ConversationId convId = (ConversationId) annotation;
               Object paramValue = invocation.getParameters()[i];
               
               String id = null;
               
               if (convId.value() != null && !"".equals(convId.value()))
               {
                  Contexts.getEventContext().set("param", paramValue);
                  id = Expressions.instance().createValueExpression(
                           convId.value()).getValue().toString();
                  Contexts.getEventContext().remove("param");
               }
               else
               {
                  id = paramValue.toString();
               }
               
               return (conversationName != null) ? conversationName + ":" + id : id;
            }
         }
      }
      
      return null;
   }
}

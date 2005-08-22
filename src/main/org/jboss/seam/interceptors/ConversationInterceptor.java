//$Id$
package org.jboss.seam.interceptors;

import java.lang.reflect.Method;
import java.util.Arrays;

import javax.ejb.AroundInvoke;
import javax.ejb.InvocationContext;

import org.jboss.logging.Logger;
import org.jboss.seam.annotations.Around;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.BeginIf;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.EndIf;
import org.jboss.seam.contexts.Contexts;

/**
 * After the end of the invocation, begin or end a long running
 * conversation, if necessary.
 * 
 * @author Gavin King
 */
@Around({ValidationInterceptor.class, BijectionInterceptor.class})
public class ConversationInterceptor extends AbstractInterceptor
{

   private static final String CONVERSATION_OWNER_NAME = "org.jboss.seam.conversationOwnerName";
   
   private static final Logger log = Logger.getLogger(ConversationInterceptor.class);

   @AroundInvoke
   public Object endOrBeginLongRunningConversation(InvocationContext invocation) throws Exception
   {
      Method method = invocation.getMethod();

      if ( isNoConversationForConversationalBean(method) )
      {
         log.info("no long-running conversation for @Conversational bean: " + component.getName());
         return component.getNoConversationOutcome();
      }
      
      Object result;
      try
      {
         result = invocation.proceed();
      }
      catch (Exception exception)
      {
         endConversationIfNecessary(method, exception);
         throw exception;
      }

      beginConversationIfNecessary(method, result);
      endConversationIfNecessary(method, result);
      return result;
   
   }


   private boolean isNoConversationForConversationalBean(Method method)
   {
      return component.isConversational() && 
            ( !Contexts.isLongRunningConversation() || !componentIsConversationOwner() ) &&
            !method.isAnnotationPresent(Begin.class) &&
            !method.isAnnotationPresent(BeginIf.class);
   }


   private boolean componentIsConversationOwner()
   {
      return component.getName().equals( Contexts.getConversationContext().get(CONVERSATION_OWNER_NAME) );
   }


   private void setConversationOwnerName()
   {
      Contexts.getConversationContext().set(CONVERSATION_OWNER_NAME, component.getName());
   }

    private void beginConversationIfNecessary(Method method, Object result)
   {
      if ( method.isAnnotationPresent(Begin.class) )
      {
         beginConversation();
      }
      else if ( method.isAnnotationPresent(BeginIf.class) )
      {
         String[] results = method.getAnnotation(BeginIf.class)
               .outcome();
         if (Arrays.asList(results).contains(result))
         {
            beginConversation();
         }
      }
   }


   private void beginConversation()
   {
      Contexts.beginConversation();
      setConversationOwnerName();
   }


  private void endConversationIfNecessary(Method method, Exception exception)
   {
      if (method.isAnnotationPresent(EndIf.class))
      {
         Class[] exceptions = method.getAnnotation(EndIf.class)
               .exception();
         if (Arrays.asList(exceptions).contains(exception.getClass()))
         {
            Contexts.endConversation();
         }
      }
   }

   private void endConversationIfNecessary(Method method, Object result)
   {
      if (method.isAnnotationPresent(End.class))
      {
         Contexts.endConversation();
      }
      else if (method.isAnnotationPresent(EndIf.class))
      {
         String[] results = method.getAnnotation(EndIf.class)
               .outcome();
         if (Arrays.asList(results).contains(result))
         {
            Contexts.endConversation();
         }
      }
   }

}

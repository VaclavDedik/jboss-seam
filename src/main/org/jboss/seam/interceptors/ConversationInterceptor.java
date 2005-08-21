//$Id$
package org.jboss.seam.interceptors;

import java.lang.reflect.Method;
import java.util.Arrays;

import javax.ejb.AroundInvoke;
import javax.ejb.InvocationContext;

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
@Around(ValidationInterceptor.class)
public class ConversationInterceptor extends AbstractInterceptor
{

   @AroundInvoke
   public Object endOrBeginLongRunningConversation(InvocationContext invocation) throws Exception
   {
      Object result;
      try
      {
         result = invocation.proceed();
      }
      catch (Exception exception)
      {
         endConversationIfNecessary(invocation.getMethod(), exception);
         throw exception;
      }

      beginConversationIfNecessary(invocation.getMethod(), result);
      endConversationIfNecessary(invocation.getMethod(), result);
      return result;
   
   }


   private void beginConversationIfNecessary(Method method, Object result)
   {
      if ( method.isAnnotationPresent(Begin.class) )
      {
         Contexts.beginConversation();
      }
      else if ( method.isAnnotationPresent(BeginIf.class) )
      {
         String[] results = method.getAnnotation(BeginIf.class)
               .outcome();
         if (Arrays.asList(results).contains(result))
         {
            Contexts.beginConversation();
         }
      }
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

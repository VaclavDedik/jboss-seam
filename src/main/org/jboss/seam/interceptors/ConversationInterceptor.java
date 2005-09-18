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
import org.jboss.seam.annotations.BeginTask;
import org.jboss.seam.annotations.CompleteTask;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.EndIf;
import org.jboss.seam.annotations.ResumeTask;
import org.jboss.seam.core.Manager;

/**
 * After the end of the invocation, begin or end a long running
 * conversation, if necessary.
 * 
 * @author Gavin King
 */
@Around({ValidationInterceptor.class, BijectionInterceptor.class})
public class ConversationInterceptor extends AbstractInterceptor
{

   private static final Logger log = Logger.getLogger(ConversationInterceptor.class);

   @AroundInvoke
   public Object endOrBeginLongRunningConversation(InvocationContext invocation) throws Exception
   {
      Method method = invocation.getMethod();

      if ( isNoConversationForConversationalBean(method) )
      {
         log.debug("no long-running conversation for @Conversational bean: " + component.getName());
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
            ( !Manager.instance().isLongRunningConversation() || !componentIsConversationOwner() ) &&
            !method.isAnnotationPresent(Begin.class) &&
            !method.isAnnotationPresent(BeginIf.class) &&
            !method.isAnnotationPresent(BeginTask.class) &&
            !method.isAnnotationPresent(Destroy.class) && 
            !method.isAnnotationPresent(Create.class); //probably superfluous
   }


   private boolean componentIsConversationOwner()
   {
      return component.getName().equals( Manager.instance().getConversationOwnerName() );
   }


   private void setConversationOwnerName()
   {
      Manager.instance().setConversationOwnerName( component.getName() );
   }

   private void beginConversationIfNecessary(Method method, Object result)
   {
      boolean simpleBegin = method.isAnnotationPresent(Begin.class) || 
            method.isAnnotationPresent(BeginTask.class) || 
            method.isAnnotationPresent(ResumeTask.class);
      if ( simpleBegin )
      {
         if (result!=null)
         {
            beginConversation();
         }
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
      log.debug("Beginning long-running conversation");
      Manager.instance().setLongRunningConversation(true);
      setConversationOwnerName();
   }


  private void endConversationIfNecessary(Method method, Exception exception)
   {
      if ( method.isAnnotationPresent(EndIf.class) )
      {
         Class[] exceptions = method.getAnnotation(EndIf.class)
               .exception();
         if (Arrays.asList(exceptions).contains(exception.getClass()))
         {
            endConversation();
         }
      }
   }


   private void endConversationIfNecessary(Method method, Object result)
   {
      if ( method.isAnnotationPresent(End.class) || method.isAnnotationPresent(CompleteTask.class) )
      {
         if (result!=null) //null outcome interpreted as redisplay
         {
            endConversation();
         }
      }
      else if ( method.isAnnotationPresent(EndIf.class) )
      {
         String[] results = method.getAnnotation(EndIf.class)
               .outcome();
         if (Arrays.asList(results).contains(result))
         {
            endConversation();
         }
      }
   }

   private void endConversation()
   {
      log.debug("Ending long-running conversation");
      Manager.instance().setLongRunningConversation(false);
   }

}

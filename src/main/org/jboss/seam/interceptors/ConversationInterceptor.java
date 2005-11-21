//$Id$
package org.jboss.seam.interceptors;

import java.lang.reflect.Method;
import java.util.Arrays;

import javax.ejb.AroundInvoke;
import javax.ejb.InvocationContext;
import javax.faces.event.PhaseId;

import org.jboss.logging.Logger;
import org.jboss.seam.annotations.Around;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.BeginTask;
import org.jboss.seam.annotations.CompleteTask;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.ResumeTask;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.Manager;

/**
 * After the end of the invocation, begin or end a long running
 * conversation, if necessary.
 * 
 * @author Gavin King
 */
@Around({ValidationInterceptor.class, BijectionInterceptor.class, OutcomeInterceptor.class})
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
      
      Object result = invocation.proceed();

      beginConversationIfNecessary(method, result);
      endConversationIfNecessary(method, result);
      return result;
   
   }


   private boolean isNoConversationForConversationalBean(Method method)
   {
      return component.isConversational() && 
            Lifecycle.getPhaseId()==PhaseId.INVOKE_APPLICATION &&
            ( !Manager.instance().isLongRunningConversation() || !componentIsConversationOwner() ) &&
            !method.isAnnotationPresent(Begin.class) &&
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
      boolean simpleBegin = 
            method.isAnnotationPresent(BeginTask.class) || 
            method.isAnnotationPresent(ResumeTask.class) ||
            ( method.isAnnotationPresent(Begin.class) && method.getAnnotation(Begin.class).ifOutcome().length==0 );
      if ( simpleBegin )
      {
         if (result!=null)
         {
            beginConversation();
         }
      }
      else if ( method.isAnnotationPresent(Begin.class) )
      {
         String[] outcomes = method.getAnnotation(Begin.class).ifOutcome();
         if ( outcomes.length==0 || Arrays.asList(outcomes).contains(result) )
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

   private void endConversationIfNecessary(Method method, Object result)
   {
      boolean simpleEnd = 
            ( method.isAnnotationPresent(End.class) && method.getAnnotation(End.class).ifOutcome().length==0 ) || 
            method.isAnnotationPresent(CompleteTask.class);
      if ( simpleEnd )
      {
         if (result!=null) //null outcome interpreted as redisplay
         {
            endConversation();
         }
      }
      else if ( method.isAnnotationPresent(End.class) )
      {
         String[] outcomes = method.getAnnotation(End.class).ifOutcome();
         if (Arrays.asList(outcomes).contains(result))
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

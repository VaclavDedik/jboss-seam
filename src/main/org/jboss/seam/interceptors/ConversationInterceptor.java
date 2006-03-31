//$Id$
package org.jboss.seam.interceptors;

import java.lang.reflect.Method;
import java.util.Arrays;

import javax.ejb.AroundInvoke;
import javax.ejb.InvocationContext;
import javax.faces.application.FacesMessage;
import javax.faces.event.PhaseId;

import org.jboss.logging.Logger;
import org.jboss.seam.annotations.Around;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.BeginTask;
import org.jboss.seam.annotations.Conversational;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.EndTask;
import org.jboss.seam.annotations.StartTask;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.core.Manager;
import org.jboss.seam.core.Pageflow;

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
         log.info("no long-running conversation for @Conversational bean: " + component.getName());
         FacesMessages.instance().addFromResourceBundle( FacesMessage.SEVERITY_WARN, "org.jboss.seam.NoConversation", "No conversation" );
         return methodIsConversational(method) ?
               method.getAnnotation(Conversational.class).ifNotBegunOutcome() :
               component.getBeanClass().getAnnotation(Conversational.class).ifNotBegunOutcome();
      }

      if ( isMissingJoin(method) )
      {
         throw new IllegalStateException("begin method invoked from a long running conversation, try using @Begin(join=true)");
      }

      Object result = invocation.proceed();

      beginConversationIfNecessary(method, result);
      endConversationIfNecessary(method, result);
      return result;
   
   }

   private boolean isMissingJoin(Method method) {
      return Manager.instance().isLongRunningConversation() && ( 
            ( 
                  method.isAnnotationPresent(Begin.class) && 
                  !method.getAnnotation(Begin.class).join() && 
                  !method.getAnnotation(Begin.class).nested() 
            ) ||
            method.isAnnotationPresent(BeginTask.class) ||
            method.isAnnotationPresent(StartTask.class) 
         );
   }

   private boolean isNoConversationForConversationalBean(Method method)
   {
      boolean classlevelViolation = componentIsConversational() && 
            Lifecycle.getPhaseId()==PhaseId.INVOKE_APPLICATION &&
            ( !Manager.instance().isLongRunningConversation() || ( componentShouldBeInitiator() && !componentIsInitiator() ) ) &&
            !method.isAnnotationPresent(Begin.class) &&
            !method.isAnnotationPresent(StartTask.class) &&
            !method.isAnnotationPresent(BeginTask.class) &&
            !method.isAnnotationPresent(Destroy.class) && 
            !method.isAnnotationPresent(Create.class); //probably superfluous
      
      if (classlevelViolation) return true;
      
      boolean methodlevelViolation = methodIsConversational(method) &&
            Lifecycle.getPhaseId()==PhaseId.INVOKE_APPLICATION &&
            ( !Manager.instance().isLongRunningConversation() || ( componentShouldBeInitiator(method) && !componentIsInitiator() ) );
      
      return methodlevelViolation;
      
   }

   private boolean methodIsConversational(Method method) {
      return method.isAnnotationPresent(Conversational.class);
   }

   private boolean componentShouldBeInitiator(Method method) {
      return method.getAnnotation(Conversational.class).initiator();
   }

   private boolean componentIsConversational() {
      return component.getBeanClass().isAnnotationPresent(Conversational.class);
   }

   private boolean componentShouldBeInitiator() {
      return component.getBeanClass().getAnnotation(Conversational.class).initiator();
   }

   private boolean componentIsInitiator()
   {
      return component.getName().equals( Manager.instance().getCurrentConversationInitiator() );
   }

   private void beginConversationIfNecessary(Method method, Object result)
   {
      
      boolean simpleBegin = 
            method.isAnnotationPresent(StartTask.class) || 
            method.isAnnotationPresent(BeginTask.class) ||
            ( method.isAnnotationPresent(Begin.class) && method.getAnnotation(Begin.class).ifOutcome().length==0 );
      if ( simpleBegin )
      {
         if ( result!=null || method.getReturnType().equals(void.class) )
         {
            boolean nested = false;
            if ( method.isAnnotationPresent(Begin.class) )
            {
               nested = method.getAnnotation(Begin.class).nested();
            }
            beginConversation( nested, getProcessDefinitionName(method) );
         }
      }
      else if ( method.isAnnotationPresent(Begin.class) )
      {
         String[] outcomes = method.getAnnotation(Begin.class).ifOutcome();
         if ( outcomes.length==0 || Arrays.asList(outcomes).contains(result) )
         {
            beginConversation( 
                  method.getAnnotation(Begin.class).nested(), 
                  getProcessDefinitionName(method) 
               );
         }
      }
      
   }

   private String getProcessDefinitionName(Method method) {
      if ( method.isAnnotationPresent(Begin.class) )
      {
         return method.getAnnotation(Begin.class).pageflow();
      }
      if ( method.isAnnotationPresent(BeginTask.class) )
      {
         return method.getAnnotation(BeginTask.class).pageflow();
      }
      if ( method.isAnnotationPresent(StartTask.class) )
      {
         return method.getAnnotation(StartTask.class).pageflow();
      }
      //TODO: let them pass a pagelfow name as a request parameter
      return "";
   }

   private void beginConversation(boolean nested, String processDefinitionName)
   {
      if ( !Manager.instance().isLongRunningConversation() )
      {
         log.debug("Beginning long-running conversation");
         Manager.instance().beginConversation( component.getName() );
         beginNavigation(processDefinitionName);
      }
      else if (nested)
      {
         log.debug("Beginning nested conversation");
         Manager.instance().beginNestedConversation( component.getName() );
         beginNavigation(processDefinitionName);
      }
   }
   
   private void beginNavigation(String processDefinitionName)
   {
      if ( !processDefinitionName.equals("") )
      {
         Pageflow.instance().begin(processDefinitionName);
      }
   }

   private void endConversationIfNecessary(Method method, Object result)
   {
      boolean simpleEnd = 
            ( method.isAnnotationPresent(End.class) && method.getAnnotation(End.class).ifOutcome().length==0 ) || 
            ( method.isAnnotationPresent(EndTask.class) && method.getAnnotation(EndTask.class).ifOutcome().length==0 );
      if ( simpleEnd )
      {
         if ( result!=null || method.getReturnType().equals(void.class) ) //null outcome interpreted as redisplay
         {
            endConversation();
         }
      }
      else if ( method.isAnnotationPresent(End.class) )
      {
         String[] outcomes = method.getAnnotation(End.class).ifOutcome();
         if ( Arrays.asList(outcomes).contains(result) )
         {
            endConversation();
         }
      }
      else if ( method.isAnnotationPresent(EndTask.class) )
      {
         //TODO: fix minor code duplication
         String[] outcomes = method.getAnnotation(EndTask.class).ifOutcome();
         if ( Arrays.asList(outcomes).contains(result) )
         {
            endConversation();
         }
      }
   }

   private void endConversation()
   {
      log.debug("Ending long-running conversation");
      Manager.instance().endConversation();
   }

}

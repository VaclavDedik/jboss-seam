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
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.EndTask;
import org.jboss.seam.annotations.StartTask;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.Jbpm;
import org.jboss.seam.core.Manager;
import org.jboss.seam.core.Pageflow;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;

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
         log.warn("no long-running conversation for @Conversational bean: " + component.getName());
         return component.getNoConversationOutcome();
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
      return component.isConversational() && 
            Lifecycle.getPhaseId()==PhaseId.INVOKE_APPLICATION &&
            ( !Manager.instance().isLongRunningConversation() || !componentIsInitiator() ) &&
            !method.isAnnotationPresent(Begin.class) &&
            !method.isAnnotationPresent(StartTask.class) &&
            !method.isAnnotationPresent(BeginTask.class) &&
            !method.isAnnotationPresent(Destroy.class) && 
            !method.isAnnotationPresent(Create.class); //probably superfluous
   }

   private boolean componentIsInitiator()
   {
      return component.getName().equals( Manager.instance().getCurrentConversationInitiator() );
   }

   private void beginConversationIfNecessary(Method method, Object result)
   {
      
      String processDefinitionName = method.isAnnotationPresent(Begin.class) ? 
            method.getAnnotation(Begin.class).processDefinition() : "";
      
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
            beginConversation(nested, processDefinitionName);
         }
      }
      else if ( method.isAnnotationPresent(Begin.class) )
      {
         String[] outcomes = method.getAnnotation(Begin.class).ifOutcome();
         if ( outcomes.length==0 || Arrays.asList(outcomes).contains(result) )
         {
            beginConversation( method.getAnnotation(Begin.class).nested(), processDefinitionName );
         }
      }
      
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
         createProcess(processDefinitionName);
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
   
   private void createProcess(String processDefinitionName)
   {
      ProcessDefinition pd = Jbpm.instance().getPageflowProcessDefinition(processDefinitionName);
      ProcessInstance pi = pd.createProcessInstance();
      Pageflow.instance().setProcessInstance(pi);
      pi.signal();
   }
   
   //TODO: copy/paste from BusinessProcessInterceptor
   /*private void createProcess(String processDefinitionName)
   {
      JbpmSession session = ManagedJbpmSession.instance();
      
      ProcessDefinition pd = session.getGraphSession().findLatestProcessDefinition(processDefinitionName);
      if ( pd == null )
      {
         throw new IllegalArgumentException( "Unknown process definition: " + processDefinitionName );
      }
      
      ProcessInstance process = pd.createProcessInstance();
      Conversation.instance().setProcessId( process.getId() );
      process.signal();
      //session.getSession().flush();
   }*/


}

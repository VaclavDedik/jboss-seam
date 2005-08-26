/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.interceptors;

import java.lang.reflect.Method;

import javax.ejb.AroundInvoke;
import javax.ejb.InvocationContext;

import org.jboss.logging.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.BeginProcess;
import org.jboss.seam.annotations.CompleteTask;
import org.jboss.seam.annotations.StartTask;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.JbpmProcess;
import org.jboss.seam.core.Manager;
import org.jboss.seam.core.JbpmTask;
import org.jbpm.taskmgmt.exe.TaskInstance;

/**
 * Interceptor which handles interpretation of jBPM-related annotations.
 *
 * @author <a href="mailto:steve@hibernate.org">Steve Ebersole </a>
 * @version $Revision$
 */
public class BusinessProcessInterceptor extends AbstractInterceptor
{
   public static final String TASK_ID_NAME = "org.jboss.seam.jbpm.taskId";
   public static final String PROCESS_ID_NAME = "org.jboss.seam.jbpm.processId";

   public static final String TASK_NAME = "currentTask";
   public static final String PROCESS_NAME = "currentProcess";

   private static final Logger log = Logger.getLogger( BusinessProcessInterceptor.class );

   @AroundInvoke
   public Object manageBusinessProcessContext(InvocationContext invocation) throws Exception
   {
      String componentName = Seam.getComponentName( invocation.getBean().getClass() );
      Method method = invocation.getMethod();
      log.trace( "Starting bpm interception [component=" + componentName + ", method=" + method.getName() + "]" );

//      beforeInvocation( componentName, invocation );
      Object result = invocation.proceed();
      afterInvocation( invocation, result );

      return result;
   }

//   private void beforeInvocation(String componentName, InvocationContext invocation)
//   {
//      Method method = invocation.getMethod();
//
//      if ( method.isAnnotationPresent( StartTask.class ) ||
//              method.isAnnotationPresent( CompleteTask.class ) )
//      {
//         String taskIdName = null;
//         if ( method.isAnnotationPresent( StartTask.class ) )
//         {
//            taskIdName = method.getAnnotation( StartTask.class ).contextName();
//         }
//         else if ( method.isAnnotationPresent( CompleteTask.class ) )
//         {
//            taskIdName = method.getAnnotation( CompleteTask.class ).contextName();
//         }
//
//         Long taskId = ( Long ) Contexts.lookupInAllContexts( taskIdName );
//         if ( taskId == null )
//         {
//            // try to find the id in conversation
//            taskId = ( Long ) Contexts.getConversationContext().get( TASK_ID_NAME );
//         }
//
//         if ( taskId != null )
//         {
//            Manager.instance().setTaskId( taskId );
//         }
//      }
//      else
//      {
//         String expectedProcessInjectionName = locateProcessInjectionName(
//                 invocation.getBean()
//         );
//         if ( expectedProcessInjectionName != null )
//         {
//            ProcessInstance process = ( ProcessInstance ) Contexts.lookupInAllContexts(
//                    expectedProcessInjectionName
//            );
//            if ( process == null )
//            {
//               process = ( ProcessInstance ) Contexts.getConversationContext().get(
//                       PROCESS_NAME
//               );
//            }
//            if ( process == null )
//            {
//               // see if we know about a process id in conversation
//               Long processId = ( Long ) Contexts.getConversationContext().get( PROCESS_ID_NAME );
//               if ( processId != null )
//               {
//                  process = loadProcess( processId );
//               }
//            }
//            Contexts.getStatelessContext().set( expectedProcessInjectionName, process );
//         }
//      }
//   }
//
//   private String locateProcessInjectionName(Object bean)
//   {
//      return null;
//   }

   private void afterInvocation(InvocationContext invocation, Object result)
   {
      Method method = invocation.getMethod();
      if ( method.isAnnotationPresent( BeginProcess.class ) )
      {
         log.trace( "encountered @BeginProcess" );
         beginProcess( method.getAnnotation( BeginProcess.class ).name() );
      }
      else if ( method.isAnnotationPresent( StartTask.class ) )
      {
         log.trace( "encountered @StartTask" );
         Long id = ( Long ) Contexts.lookupInAllContexts( method.getAnnotation( StartTask.class ).contextName() );
         startTask( id );
      }
      else if ( method.isAnnotationPresent( CompleteTask.class ) )
      {
         log.trace( "encountered @CompleteTask" );
         CompleteTask tag = method.getAnnotation( CompleteTask.class );
         Long id = ( Long ) Contexts.lookupInAllContexts( tag.contextName() );
         String transitionName = determineCompleteTaskTransition( result, tag.transitionMap() );
         completeTask( id, transitionName );
      }
   }

   private void beginProcess(String processDefinitionName)
   {
      Contexts.getEventContext().set( JbpmProcess.DEFINITION_NAME, processDefinitionName );
      Component.getInstance( JbpmProcess.class, true );
   }

   private void startTask(Long id)
   {
      TaskInstance task = getTask( id );
      task.start();
   }

   private void completeTask(Long id, String transitionName)
   {
      TaskInstance task = getTask( id );
      if ( transitionName == null )
      {
         task.end();
      }
      else
      {
         task.end( transitionName );
      }
   }

   private TaskInstance getTask(Long id)
   {
      if ( id == null )
      {
         // TODO : actually should probably be an error if the ann points to a task id which cannot be found
         id = Manager.instance().getTaskId();
         if ( id == null )
         {
            throw new IllegalStateException( "could not determine task id" );
         }
      }
      else
      {
         Manager.instance().setTaskId( id );
      }
      return ( TaskInstance ) Component.getInstance( JbpmTask.class, true );
   }

   private String determineCompleteTaskTransition(Object result, String[] mappings)
   {
      final String resultKey = result + "=>";
      String transition = null;
      for ( final String mapping : mappings )
      {
         if ( mapping.startsWith( resultKey ) )
         {
            transition = mapping.substring( resultKey.length() );
            break;
         }
      }
      return transition;
   }

}

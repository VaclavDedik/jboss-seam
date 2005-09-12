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
import javax.faces.context.FacesContext;

import org.jboss.logging.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.CompleteTask;
import org.jboss.seam.annotations.StartTask;
import org.jboss.seam.annotations.ResumeTask;
import org.jboss.seam.annotations.ResumeProcess;
import org.jboss.seam.annotations.CreateProcess;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Manager;
import org.jboss.seam.core.ManagedJbpmSession;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.db.JbpmSession;

/**
 * Interceptor which handles interpretation of jBPM-related annotations.
 *
 * @author <a href="mailto:steve@hibernate.org">Steve Ebersole </a>
 * @version $Revision$
 */
public class BusinessProcessInterceptor extends AbstractInterceptor
{
   public static final String DEF_TASK_NAME = "currentTask";
   public static final String DEF_PROCESS_NAME = "currentProcess";
   public static final String JBPM_CONTEXT_NAME = ".jbpmContext";

   private static final Logger log = Logger.getLogger( BusinessProcessInterceptor.class );

   @AroundInvoke
   public Object manageBusinessProcessContext(InvocationContext invocation) throws Exception
   {
      String componentName = Seam.getComponentName( invocation.getBean().getClass() );
      Method method = invocation.getMethod();
      log.trace( "Starting bpm interception [component=" + componentName + ", method=" + method.getName() + "]" );

      beforeInvocation( invocation );
      Object result = invocation.proceed();
      afterInvocation( invocation, result );

      return result;
   }

   private void beforeInvocation(InvocationContext invocationContext) {
      Method method = invocationContext.getMethod();

      if ( method.isAnnotationPresent( StartTask.class ) ) {
         log.trace( "encountered @StartTask" );
         StartTask tag = method.getAnnotation( StartTask.class );
         Long taskId = ( Long ) Contexts.lookupInStatefulContexts( tag.taskIdName() );
         prepareForTask( taskId, tag.taskName(), tag.processName() );
      }
      else if ( method.isAnnotationPresent( ResumeTask.class ) ) {
         log.trace( "encountered @ResumeTask" );
         ResumeTask tag = method.getAnnotation( ResumeTask.class );
         Long taskId = ( Long ) Contexts.lookupInStatefulContexts( tag.taskIdName() );
         prepareForTask( taskId, tag.taskName(), tag.processName() );
      }
      else if ( method.isAnnotationPresent( ResumeProcess.class ) ) {
         log.trace( "encountered @ResumeProcess" );
         ResumeProcess tag = method.getAnnotation( ResumeProcess.class );
         Long processId = ( Long ) Contexts.lookupInStatefulContexts( tag.processIdName() );
         prepareForProcess( processId, tag.processName() );
      }
      else
      {
         Manager manager = Manager.instance();
         if ( !manager.isLongRunningConversation() ) return;

         log.trace( "Checking manager for possible restoration" );
         if ( manager.getTaskId() != null
                 && manager.getTaskName() != null
                 && manager.getProcessName() != null )
         {
            prepareForTask(
                    manager.getTaskId(),
                    manager.getTaskName(),
                    manager.getProcessName()
            );
         }
         else if ( manager.getProcessId() != null && manager.getProcessName() != null )
         {
            prepareForProcess( manager.getProcessId(), manager.getProcessName() );
         }
      }
   }

   private void prepareForTask(Long taskId, String taskName, String processName)
   {
      TaskInstance task = loadTask( taskId );
      exposeState( task, taskName, processName );
   }

   private void exposeState(TaskInstance task, String taskName, String processName)
   {
      if ( "".equals( taskName ) )
      {
         taskName = DEF_TASK_NAME;
      }
      log.trace( "binding task to event context [" + taskName + "]" );
      Contexts.getEventContext().set( taskName, task );

      ProcessInstance process = task.getTaskMgmtInstance().getProcessInstance();

      exposeState(process, processName );

      Manager manager = Manager.instance();
      manager.setTaskId( task.getId() );
      manager.setTaskName( taskName );
   }

   private void exposeState(ProcessInstance process, String processName)
   {
      if ( "".equals( processName ) )
      {
         processName = DEF_PROCESS_NAME;
      }
      log.trace( "binding process to event context [" + processName + "]" );
      Contexts.getEventContext().set( processName, process );

      Contexts.getEventContext().set( JBPM_CONTEXT_NAME, process.getContextInstance() );

      Manager manager = Manager.instance();
      manager.setTaskId( null );
      manager.setTaskName( null );
      manager.setProcessId( process.getId() );
      manager.setProcessName( processName );
   }

   private void prepareForProcess(Long processId, String processName)
   {
      ProcessInstance process = loadProcess( processId );
      exposeState( process, processName );
   }

   private TaskInstance loadTask(Long taskId)
   {
      JbpmSession session = ( JbpmSession )
              Component.getInstance( ManagedJbpmSession.class, true );
      return session.getTaskMgmtSession().loadTaskInstance( taskId );
   }

   private ProcessInstance loadProcess(Long processId)
   {
      JbpmSession session = ( JbpmSession )
              Component.getInstance( ManagedJbpmSession.class, true );
      return session.getGraphSession().loadProcessInstance( processId );
   }

   private void afterInvocation(InvocationContext invocation, Object result)
   {
      Method method = invocation.getMethod();
      if ( method.isAnnotationPresent( CreateProcess.class ) )
      {
         log.trace( "encountered @CreateProcess" );
         CreateProcess tag = method.getAnnotation( CreateProcess.class );
         createProcess( tag.definition(), tag.processName() );
      }
      else if ( method.isAnnotationPresent( StartTask.class ) )
      {
         log.trace( "encountered @StartTask" );
         StartTask tag = method.getAnnotation( StartTask.class );
         startTask( tag.taskName(), tag.actorExpression() );
      }
      else if ( method.isAnnotationPresent( CompleteTask.class ) )
      {
         log.trace( "encountered @CompleteTask" );
         CompleteTask tag = method.getAnnotation( CompleteTask.class );
         String transitionName = determineCompleteTaskTransition( result, tag.transitionMap() );
         completeTask( tag.name(), transitionName );
      }
   }

   private void createProcess(String processDefinitionName, String processName)
   {
      JbpmSession session = ( JbpmSession )
              Component.getInstance( ManagedJbpmSession.class, true );
      ProcessDefinition pd = session.getGraphSession()
              .findLatestProcessDefinition( processDefinitionName );
      ProcessInstance process = new ProcessInstance( pd );
      process.signal();
      session.getGraphSession().saveProcessInstance( process );

      exposeState( process, processName );
   }

   private void startTask(String taskName, String actorExpression)
   {
      String actorId = null;
      if ( !"".equals( actorExpression ) )
      {
         FacesContext facesCtx = FacesContext.getCurrentInstance();
         Object resolvedValue = facesCtx.getApplication()
                 .createValueBinding( actorExpression )
                 .getValue( facesCtx );
         if ( !( resolvedValue instanceof String ) )
         {
            throw new IllegalStateException( "Actor expression did not resolve to a string value for 'actorId'" );
         }
         actorId = ( String ) resolvedValue;
      }
      TaskInstance task = locateTaskByName( taskName );
      if ( actorId != null )
      {
         task.start( actorId );
      }
      else
      {
         task.start();
      }
   }

   private void completeTask(String taskName, String transitionName)
   {
      TaskInstance task = locateTaskByName( taskName );
      if ( transitionName == null )
      {
         task.end();
      }
      else
      {
         task.end( transitionName );
      }
      Manager manager = Manager.instance();
      manager.setTaskId( null );
      manager.setTaskName( null );
   }

   private TaskInstance locateTaskByName(String name)
   {
      return ( TaskInstance ) Contexts.getEventContext().get( name );
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

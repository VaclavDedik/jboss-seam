/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.interceptors;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.Method;
import java.util.Map;

import javax.ejb.AroundInvoke;
import javax.ejb.InvocationContext;
import javax.faces.context.FacesContext;

import org.jboss.logging.Logger;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Around;
import org.jboss.seam.annotations.BeginTask;
import org.jboss.seam.annotations.CreateProcess;
import org.jboss.seam.annotations.EndTask;
import org.jboss.seam.annotations.ResumeProcess;
import org.jboss.seam.annotations.StartTask;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Actor;
import org.jboss.seam.core.ManagedJbpmContext;
import org.jboss.seam.core.Process;
import org.jboss.seam.core.Transition;
import org.jbpm.JbpmContext;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;

/**
 * Interceptor which handles interpretation of jBPM-related annotations.
 *
 * @author <a href="mailto:steve@hibernate.org">Steve Ebersole </a>
 * @version $Revision$
 */
@Around({ValidationInterceptor.class, BijectionInterceptor.class, OutcomeInterceptor.class})
public class BusinessProcessInterceptor extends AbstractInterceptor
{
   private static final Logger log = Logger.getLogger( BusinessProcessInterceptor.class );

   @AroundInvoke
   public Object manageBusinessProcessContext(InvocationContext invocation) throws Exception
   {
      /*Actor actor = Actor.instance();
      boolean isActor = actor!=null && actor.getId()!=null;
      if (isActor) JbpmAuthentication.pushAuthenticatedActorId( actor.getId() );
      try
      {*/
         String componentName = Seam.getComponentName( invocation.getBean().getClass() );
         Method method = invocation.getMethod();
         log.trace( "Starting bpm interception [component=" + componentName + ", method=" + method.getName() + "]" );
   
         beforeInvocation( invocation );
         return afterInvocation( invocation, invocation.proceed() );
      /*}
      finally
      {
         if (isActor) JbpmAuthentication.popAuthenticatedActorId();
      }*/
   }

   private void beforeInvocation(InvocationContext invocationContext) {
      Method method = invocationContext.getMethod();
      if ( method.isAnnotationPresent( StartTask.class ) ) {
         log.trace( "encountered @StartTask" );
         StartTask tag = method.getAnnotation( StartTask.class );
         initTask( tag.taskIdParameter() );
      }
      else if ( method.isAnnotationPresent( BeginTask.class ) ) {
         log.trace( "encountered @BeginTask" );
         BeginTask tag = method.getAnnotation( BeginTask.class );
         initTask( tag.taskIdParameter() );
      }
      else if ( method.isAnnotationPresent( ResumeProcess.class ) ) {
         log.trace( "encountered @ResumeProcess" );
         ResumeProcess tag = method.getAnnotation( ResumeProcess.class );
         initProcess( tag.processIdParameter() );
      }
   }

   private void initProcess(String processIdParameter) {
      Process.instance().setProcessId( getRequestParamValueAsLong(processIdParameter) );
   }

   private void initTask(String taskIdParameter) {
      Process context = Process.instance();
      context.setTaskId( getRequestParamValueAsLong(taskIdParameter) );
      TaskInstance taskInstance = org.jboss.seam.core.TaskInstance.instance();
      context.setProcessId( taskInstance.getTaskMgmtInstance().getProcessInstance().getId() );
   }

   private Object afterInvocation(InvocationContext invocation, Object result)
   {
      if (result!=null) //interpreted as "redisplay"
      {
         Method method = invocation.getMethod();
         if ( method.isAnnotationPresent( CreateProcess.class ) )
         {
            log.trace( "encountered @CreateProcess" );
            CreateProcess tag = method.getAnnotation( CreateProcess.class );
            createProcess( tag.definition() );
         }
         else if ( method.isAnnotationPresent( StartTask.class ) )
         {
            log.trace( "encountered @StartTask" );
            //StartTask tag = method.getAnnotation( StartTask.class );
            startTask();
         }
         else if ( method.isAnnotationPresent( EndTask.class ) )
         {
            log.trace( "encountered @EndTask" );
            completeTask( method.getAnnotation(EndTask.class).transition() );
         }
      }
      return result;
   }

   private void createProcess(String processDefinitionName)
   {
      JbpmContext jbpmContext = ManagedJbpmContext.instance();
      
      ProcessDefinition pd = jbpmContext.getGraphSession().findLatestProcessDefinition(processDefinitionName);
      if ( pd == null )
      {
         throw new IllegalArgumentException( "Unknown process definition: " + processDefinitionName );
      }
      
      ProcessInstance process = pd.createProcessInstance();
      jbpmContext.save(process);
      Process.instance().setProcessId( process.getId() );
      // need to set process variables before the signal
      Contexts.getBusinessProcessContext().flush();
      process.signal();
      //ManagedJbpmContext.instance().getSession().flush();
   }

   private void startTask()
   {
      String actorId = Actor.instance().getId();
      TaskInstance task = org.jboss.seam.core.TaskInstance.instance();
      if ( actorId != null )
      {
         task.start(actorId);
      }
      else
      {
         task.start();
      }
   }

   private void completeTask(String transitionName)
   {
      TaskInstance task = org.jboss.seam.core.TaskInstance.instance();
      if ( task == null )
      {
         throw new IllegalStateException( "no task instance associated with context" );
      }
      
      if ( "".equals(transitionName) )
      {
         transitionName = Transition.instance().getName();
      }
      
      if ( transitionName == null )
      {
         task.end();
      }
      else
      {
         task.end(transitionName);
      }
      
      Process.instance().setTaskId(null);
      //ManagedJbpmContext.instance().getSession().flush();
   }

    private Long getRequestParamValueAsLong(String paramName)
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map paramMap = facesContext.getExternalContext()
              .getRequestParameterMap();
        String paramValue = (String) paramMap.get(paramName);
        PropertyEditor editor = PropertyEditorManager.findEditor(Long.class);
        if ( editor != null )
        {
            editor.setAsText(paramValue);
            return (Long) editor.getValue();
        }
        else
        {
            return Long.parseLong(paramValue);
        }
    }
}

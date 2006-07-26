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

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.annotations.BeginTask;
import org.jboss.seam.annotations.CreateProcess;
import org.jboss.seam.annotations.EndTask;
import org.jboss.seam.annotations.Interceptor;
import org.jboss.seam.annotations.ResumeProcess;
import org.jboss.seam.annotations.StartTask;
import org.jboss.seam.core.BusinessProcess;
import org.jboss.seam.core.FacesMessages;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;

/**
 * Interceptor which handles interpretation of jBPM-related annotations.
 *
 * @author <a href="mailto:steve@hibernate.org">Steve Ebersole </a>
 * @version $Revision$
 */
@Interceptor(around={ValidationInterceptor.class, BijectionInterceptor.class, OutcomeInterceptor.class})
public class BusinessProcessInterceptor extends AbstractInterceptor
{
   
   private static final Log log = LogFactory.getLog( BusinessProcessInterceptor.class );

   @AroundInvoke
   public Object manageBusinessProcessContext(InvocationContext invocation) throws Exception
   {
      /*Actor actor = Actor.instance();
      boolean isActor = actor!=null && actor.getId()!=null;
      if (isActor) JbpmAuthentication.pushAuthenticatedActorId( actor.getId() );
      try
      {*/
      if ( !beforeInvocation(invocation) )
      {
         return null;
      }
      else
      {
         return afterInvocation( invocation, invocation.proceed() );
      }
      /*}
      finally
      {
         if (isActor) JbpmAuthentication.popAuthenticatedActorId();
      }*/
   }

   private boolean beforeInvocation(InvocationContext invocationContext) {
      Method method = invocationContext.getMethod();
      if ( method.isAnnotationPresent( StartTask.class ) ) {
         log.trace( "encountered @StartTask" );
         StartTask tag = method.getAnnotation( StartTask.class );
         return initTask( tag.taskIdParameter() );
      }
      else if ( method.isAnnotationPresent( BeginTask.class ) ) {
         log.trace( "encountered @BeginTask" );
         BeginTask tag = method.getAnnotation( BeginTask.class );
         return initTask( tag.taskIdParameter() );
      }
      else if ( method.isAnnotationPresent( ResumeProcess.class ) ) {
         log.trace( "encountered @ResumeProcess" );
         ResumeProcess tag = method.getAnnotation( ResumeProcess.class );
         return initProcess( tag.processIdParameter() );
      }
      if ( method.isAnnotationPresent(EndTask.class) )
      {
         log.trace( "encountered @EndTask" );
         return checkTask();
      }
      else
      {
         return true;
      }
   }

   private boolean checkTask()
   {
      TaskInstance task = org.jboss.seam.core.TaskInstance.instance();
      Long taskId = BusinessProcess.instance().getTaskId();
      if ( task==null )
      {
         taskNotFound(taskId);
         return false;
      }
      else if ( task.hasEnded() )
      {
         taskEnded(taskId);
         return false;
      }
      else
      {
         return true;
      }
   }

   private boolean initTask(String taskIdParameter) {
      BusinessProcess process = BusinessProcess.instance();
      Long taskId = getRequestParamValueAsLong(taskIdParameter);
      process.setTaskId(taskId);
      TaskInstance taskInstance = org.jboss.seam.core.TaskInstance.instance();
      if (taskInstance==null)
      {
         taskNotFound(taskId);
         return false;
      }
      else if ( taskInstance.hasEnded() )
      {
         taskEnded(taskId);
         return false;
      }
      else
      {
         process.setProcessId( taskInstance.getTaskMgmtInstance().getProcessInstance().getId() );
         return true;
      }
   }

   private boolean initProcess(String processIdParameter) {
      Long processId = getRequestParamValueAsLong(processIdParameter);
      BusinessProcess.instance().setProcessId(processId);
      ProcessInstance process = org.jboss.seam.core.ProcessInstance.instance();
      if ( process==null )
      {
         processNotFound(processId);
         return false;
      }
      else if ( process.hasEnded() )
      {
         processEnded(processId);
         return false;
      }
      else
      {
         return true;
      }
   }

   private void taskNotFound(Long taskId)
   {
      FacesMessages.instance().addFromResourceBundle(
            FacesMessage.SEVERITY_WARN, 
            "org.jboss.seam.TaskNotFound", 
            "Task #0 not found", 
            taskId
         );
   }

   private void taskEnded(Long taskId)
   {
      FacesMessages.instance().addFromResourceBundle(
            FacesMessage.SEVERITY_WARN, 
            "org.jboss.seam.TaskEnded", 
            "Task #0 already ended", 
            taskId
         );
   }

   private void processEnded(Long processId)
   {
      FacesMessages.instance().addFromResourceBundle(
            FacesMessage.SEVERITY_WARN, 
            "org.jboss.seam.ProcessEnded", 
            "Process #0 already ended", 
            processId
         );
   }

   private void processNotFound(Long processId)
   {
      FacesMessages.instance().addFromResourceBundle(
            FacesMessage.SEVERITY_WARN, 
            "org.jboss.seam.ProcessNotFound", 
            "Process #0 not found", 
            processId
         );
   }

   private Object afterInvocation(InvocationContext invocation, Object result)
   {
      Method method = invocation.getMethod();
      if ( result!=null || method.getReturnType().equals(void.class) ) //interpreted as "redisplay"
      {
         if ( method.isAnnotationPresent( CreateProcess.class ) )
         {
            log.trace( "encountered @CreateProcess" );
            CreateProcess tag = method.getAnnotation(CreateProcess.class);
            BusinessProcess.instance().createProcess( tag.definition() );
         }
         if ( method.isAnnotationPresent(StartTask.class) )
         {
            log.trace( "encountered @StartTask" );
            BusinessProcess.instance().startTask();
         }
         if ( method.isAnnotationPresent(EndTask.class) )
         {
            log.trace( "encountered @EndTask" );
            BusinessProcess.instance().endTask( method.getAnnotation(EndTask.class).transition() );
         }
         if ( method.isAnnotationPresent(org.jboss.seam.annotations.Transition.class) )
         {
            log.trace( "encountered @Transition" );
            String name = method.getAnnotation(org.jboss.seam.annotations.Transition.class).value();
            org.jboss.seam.core.ProcessInstance.instance().signal(name);
         }
      }
      return result;
   }

   private Long getRequestParamValueAsLong(String paramName)
   {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map paramMap = facesContext.getExternalContext()
              .getRequestParameterMap();
        String paramValue = (String) paramMap.get(paramName);
        if (paramValue==null)
        {
           return null;
        }
        else
        {
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
}

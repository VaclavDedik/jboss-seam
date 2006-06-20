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

import javax.faces.context.FacesContext;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.annotations.Around;
import org.jboss.seam.annotations.BeginTask;
import org.jboss.seam.annotations.CreateProcess;
import org.jboss.seam.annotations.EndTask;
import org.jboss.seam.annotations.ResumeProcess;
import org.jboss.seam.annotations.StartTask;
import org.jboss.seam.core.BusinessProcess;
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
   
   private static final Log log = LogFactory.getLog( BusinessProcessInterceptor.class );

   @AroundInvoke
   public Object manageBusinessProcessContext(InvocationContext invocation) throws Exception
   {
      /*Actor actor = Actor.instance();
      boolean isActor = actor!=null && actor.getId()!=null;
      if (isActor) JbpmAuthentication.pushAuthenticatedActorId( actor.getId() );
      try
      {*/
         Method method = invocation.getMethod();
         log.trace( "Starting bpm interception [component=" + component.getName() + ", method=" + method.getName() + "]" );
   
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
      BusinessProcess.instance().setProcessId( getRequestParamValueAsLong(processIdParameter) );
   }

   private void initTask(String taskIdParameter) {
      BusinessProcess process = BusinessProcess.instance();
      process.setTaskId( getRequestParamValueAsLong(taskIdParameter) );
      TaskInstance taskInstance = org.jboss.seam.core.TaskInstance.instance();
      process.setProcessId( taskInstance.getTaskMgmtInstance().getProcessInstance().getId() );
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

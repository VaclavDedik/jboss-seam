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

import org.jboss.seam.annotations.AroundInvoke;
import org.jboss.seam.annotations.BeginTask;
import org.jboss.seam.annotations.CreateProcess;
import org.jboss.seam.annotations.EndTask;
import org.jboss.seam.annotations.Interceptor;
import org.jboss.seam.annotations.ResumeProcess;
import org.jboss.seam.annotations.StartTask;
import org.jboss.seam.core.BusinessProcess;
import org.jboss.seam.intercept.InvocationContext;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

/**
 * Interceptor which handles interpretation of jBPM-related annotations.
 *
 * @author <a href="mailto:steve@hibernate.org">Steve Ebersole </a>
 * @version $Revision$
 */
@Interceptor(stateless=true,
             around={ValidationInterceptor.class, BijectionInterceptor.class})
public class BusinessProcessInterceptor extends AbstractInterceptor
{
   private static final long serialVersionUID = 758197867958840918L;
   
   private static final LogProvider log = Logging.getLogProvider( BusinessProcessInterceptor.class );

   @AroundInvoke
   public Object aroundInvoke(InvocationContext invocation) throws Exception
   {
      if ( !beforeInvocation(invocation) )
      {
         return null;
      }
      else
      {
         return afterInvocation( invocation, invocation.proceed() );
      }
   }

   private boolean beforeInvocation(InvocationContext invocationContext) {
      Method method = invocationContext.getMethod();
      if ( method.isAnnotationPresent( StartTask.class ) ) {
         log.trace( "encountered @StartTask" );
         StartTask tag = method.getAnnotation( StartTask.class );
         Long taskId = getRequestParamValueAsLong( tag.taskIdParameter() );
         return BusinessProcess.instance().resumeTask(taskId);
      }
      else if ( method.isAnnotationPresent( BeginTask.class ) ) {
         log.trace( "encountered @BeginTask" );
         BeginTask tag = method.getAnnotation( BeginTask.class );
         Long taskId = getRequestParamValueAsLong( tag.taskIdParameter() );
         return BusinessProcess.instance().resumeTask(taskId);
      }
      else if ( method.isAnnotationPresent( ResumeProcess.class ) ) {
         log.trace( "encountered @ResumeProcess" );
         ResumeProcess tag = method.getAnnotation( ResumeProcess.class );
         Long processId = getRequestParamValueAsLong( tag.processIdParameter() );
         return BusinessProcess.instance().resumeProcess(processId);
      }
      if ( method.isAnnotationPresent(EndTask.class) )
      {
         log.trace( "encountered @EndTask" );
         return BusinessProcess.instance().validateTask();
      }
      else
      {
         return true;
      }
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
            String transitionName = method.getAnnotation(org.jboss.seam.annotations.Transition.class).value();
            if ( "".equals(transitionName) ) transitionName = method.getName();
            BusinessProcess.instance().transition(transitionName);
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
           throw new IllegalStateException("no value for request parameter: " + paramName);
           //return null;
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

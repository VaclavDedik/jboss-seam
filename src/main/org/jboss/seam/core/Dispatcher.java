package org.jboss.seam.core;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Date;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.interceptor.Interceptors;
import javax.interceptor.InvocationContext;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.timer.Duration;
import org.jboss.seam.annotations.timer.Expiration;
import org.jboss.seam.annotations.timer.IntervalDuration;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.ejb.SeamInterceptor;
import org.jboss.seam.util.Reflections;

/**
 * Dispatcher for asynchronous methods.
 * 
 * @author Gavin King
 *
 */
@Stateless
@Name("org.jboss.seam.core.dispatcher")
@Interceptors(SeamInterceptor.class)
public class Dispatcher implements LocalDispatcher
{
   
   public static final String EXECUTING_ASYNCHRONOUS_CALL = "org.jboss.seam.core.executingAsynchronousCall";
   
   @Resource TimerService timerService;
   
   static class AsynchronousInvocation implements Serializable
   {
      static final long serialVersionUID = 7426196491669891310L;
      
      private String methodName;
      private Class[] argTypes;
      private Object[] args;
      private String componentName;
      private Long processId;
      private Long taskId;
      
      public AsynchronousInvocation(Method method, String componentName, Object[] args)
      {
         this.methodName = method.getName();
         this.argTypes = method.getParameterTypes();
         this.args = args==null ? new Object[0] : args;
         this.componentName = componentName;
         if ( Init.instance().isJbpmInstalled() )
         {
            processId = BusinessProcess.instance().getProcessId();
            taskId = BusinessProcess.instance().getTaskId();
         }
      }
      
      public void execute(Timer timer)
      {
         
         //TODO: shouldn't this take place in a Seam context anyway??!? (bug in EJB3?)
         
         Lifecycle.beginCall();
         Contexts.getEventContext().set(EXECUTING_ASYNCHRONOUS_CALL, true);
         try
         {
            if (taskId!=null)
            {
               BusinessProcess.instance().initTask(taskId);
            }
            else if (processId!=null)
            {
               BusinessProcess.instance().initProcess(processId);
            }
            
            Contexts.getEventContext().set("timer", timer);
         
            Object target = Component.getInstance(componentName);
            
            Method method;
            try
            {
               method = target.getClass().getMethod(methodName, argTypes);
            }
            catch (NoSuchMethodException nsme)
            {
               throw new IllegalStateException(nsme);
            }
            
            Reflections.invokeAndWrap(method, target, args);
            
         }
         finally
         {
            Contexts.getEventContext().remove(EXECUTING_ASYNCHRONOUS_CALL);
            Lifecycle.endCall();
         }
         
      }
   }
   
   @Timeout
   public void dispatch(Timer timer)
   {
      ( (AsynchronousInvocation) timer.getInfo() ).execute(timer);
   }
   
   public Timer schedule(InvocationContext invocation, Component component)
   {
      Long duration = 0l;
      Date expiration = null;
      Long intervalDuration = null;
      Annotation[][] parameterAnnotations = invocation.getMethod().getParameterAnnotations();
      for ( int i=0; i<parameterAnnotations.length; i++ )
      {
         Annotation[] annotations = parameterAnnotations[i];
         for (Annotation annotation: annotations)
         {
            if ( annotation.annotationType().equals(Duration.class) )
            {
               duration = (Long) invocation.getParameters()[i];
            }
            else if ( annotation.annotationType().equals(IntervalDuration.class) )
            {
               intervalDuration = (Long) invocation.getParameters()[i];
            }
            else if ( annotation.annotationType().equals(Expiration.class) )
            {
               expiration = (Date) invocation.getParameters()[i];
            }
         }
      }
      AsynchronousInvocation asynchronousInvocation = new AsynchronousInvocation(
            invocation.getMethod(), 
            component.getName(), 
            invocation.getParameters()
         );
      
      if (intervalDuration!=null)
      {
         if (expiration!=null)
         {
            return timerService.createTimer(expiration, intervalDuration, asynchronousInvocation);
         }
         else
         {
            return timerService.createTimer(duration, intervalDuration, asynchronousInvocation);
         }            
      }
      else if (expiration!=null)
      {
         return timerService.createTimer(expiration, asynchronousInvocation);
      }
      else
      {
         return timerService.createTimer(duration, asynchronousInvocation);
      }
      
   }

   public static LocalDispatcher instance()
   {
      if ( !Contexts.isApplicationContextActive() )
      {
         throw new IllegalStateException("no application context active");
      }
      return (LocalDispatcher) Component.getInstance(Dispatcher.class);         
   }

}

package org.jboss.seam.core;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Date;

import javax.annotation.PostConstruct;
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
   
   public static abstract class Asynchronous implements Serializable
   {
      static final long serialVersionUID = -551286304424595765L;
      
      private Long processId;
      private Long taskId;
      
      protected Asynchronous()
      {
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
         
            call();
            
         }
         finally
         {
            Contexts.getEventContext().remove(EXECUTING_ASYNCHRONOUS_CALL);
            Lifecycle.endCall();
         }
         
      }
      
      protected abstract void call();
   }
   
   static class AsynchronousInvocation extends Asynchronous
   {
      static final long serialVersionUID = 7426196491669891310L;
      
      private String methodName;
      private Class[] argTypes;
      private Object[] args;
      private String componentName;
      
      public AsynchronousInvocation(Method method, String componentName, Object[] args)
      {
         this.methodName = method.getName();
         this.argTypes = method.getParameterTypes();
         this.args = args==null ? new Object[0] : args;
         this.componentName = componentName;
      }
      
      @Override
      protected void call()
      {
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
   }
   
   static class AsynchronousEvent extends Asynchronous
   {
      static final long serialVersionUID = 2074586442931427819L;
      
      private String type;

      public AsynchronousEvent(String type)
      {
         this.type = type;
      }

      @Override
      public void call()
      {
         Events.instance().raiseEvent(type);
      }
      
   }
   
   @PostConstruct 
   public void postConstruct() {} //workaround for a bug in EJB3
   
   @Timeout
   public void dispatch(Timer timer)
   {
      ( (Asynchronous) timer.getInfo() ).execute(timer);
   }
   
   public Timer scheduleEvent(String type, Long duration, Date expiration, Long intervalDuration)
   {
      return schedule( duration, expiration, intervalDuration, new AsynchronousEvent(type) );
   }
   
   public Timer scheduleInvocation(InvocationContext invocation, Component component)
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
      
      return schedule(duration, expiration, intervalDuration, asynchronousInvocation);
      
   }

   private Timer schedule(Long duration, Date expiration, Long intervalDuration, Asynchronous asynchronous)
   {
      if (intervalDuration!=null)
      {
         if (expiration!=null)
         {
            return timerService.createTimer(expiration, intervalDuration, asynchronous);
         }
         else
         {
            return timerService.createTimer(duration, intervalDuration, asynchronous);
         }            
      }
      else if (expiration!=null)
      {
         return timerService.createTimer(expiration, asynchronous);
      }
      else
      {
         return timerService.createTimer(duration, asynchronous);
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

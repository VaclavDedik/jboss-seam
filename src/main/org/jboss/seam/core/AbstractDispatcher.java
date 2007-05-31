package org.jboss.seam.core;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Date;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.timer.Duration;
import org.jboss.seam.annotations.timer.Expiration;
import org.jboss.seam.annotations.timer.IntervalDuration;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.intercept.InvocationContext;
import org.jboss.seam.util.Reflections;

/**
 * Abstract Dispatcher implementation
 * 
 * @author Gavin King
 *
 */
public abstract class AbstractDispatcher<T, S> implements Dispatcher<T, S>
{
   
   public static final String EXECUTING_ASYNCHRONOUS_CALL = "org.jboss.seam.core.executingAsynchronousCall";
      
   public static Dispatcher instance()
   {
      if ( !Contexts.isApplicationContextActive() )
      {
         throw new IllegalStateException("no application context active");
      }
      return (Dispatcher) Component.getInstance("org.jboss.seam.core.dispatcher");         
   }

   public static abstract class Asynchronous implements Serializable
   {
      static final long serialVersionUID = -551286304424595765L;
      
      private Long processId;
      private Long taskId;
      
      protected Asynchronous()
      {
         if ( Init.instance().isJbpmInstalled() )
         {
            BusinessProcess businessProcess = BusinessProcess.instance();
            processId = businessProcess.getProcessId();
            taskId = BusinessProcess.instance().getTaskId();
         }        
      }
      
      public void execute(Object timer)
      {
         
         //TODO: shouldn't this take place in a Seam context anyway??!? (bug in EJB3?)
         
         Lifecycle.beginCall();
         Contexts.getEventContext().set(EXECUTING_ASYNCHRONOUS_CALL, true);
         try
         {
            if (taskId!=null)
            {
               BusinessProcess.instance().resumeTask(taskId);
            }
            else if (processId!=null)
            {
               BusinessProcess.instance().resumeProcess(processId);
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
      
      public AsynchronousInvocation(InvocationContext invocation, Component component)
      {
         this( invocation.getMethod(), component.getName(), invocation.getParameters() );
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
      private Object[] parameters;

      public AsynchronousEvent(String type, Object[] parameters)
      {
         this.type = type;
         this.parameters = parameters;
      }

      @Override
      public void call()
      {
         Events.instance().raiseEvent(type, parameters);
      }
      
   }

   protected TimerSchedule createSchedule(InvocationContext invocation)
   {
      Long duration = null;
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
      
      TimerSchedule schedule = new TimerSchedule(duration, expiration, intervalDuration);
      return schedule;
   }
   
}

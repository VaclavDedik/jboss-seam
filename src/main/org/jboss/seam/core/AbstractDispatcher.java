package org.jboss.seam.core;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
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
   
    //TODO: move down to TimerServiceDispatcher!
    public Object call(Callable task) 
    {
        try 
        {
            return task.call();
        } 
        catch (RuntimeException e) 
        {
            // just pass along runtime exceptions
            throw e;
        } 
        catch (Exception e) 
        {
            throw new RuntimeException(e);
        }
    }
}

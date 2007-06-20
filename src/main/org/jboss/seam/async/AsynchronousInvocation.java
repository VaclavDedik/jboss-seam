package org.jboss.seam.async;

import java.lang.reflect.Method;

import org.jboss.seam.Component;
import org.jboss.seam.intercept.InvocationContext;
import org.jboss.seam.util.Reflections;

public class AsynchronousInvocation extends Asynchronous
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
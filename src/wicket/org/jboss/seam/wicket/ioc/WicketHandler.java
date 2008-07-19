package org.jboss.seam.wicket.ioc;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.wicket.WicketComponent;


public class WicketHandler implements Serializable
{
   
   public static WicketHandler create(Object bean)
   {
      WicketHandler handler = new WicketHandler(bean.getClass());
      return handler;
   }
   
   public WicketHandler(Class<?> type)
   {
      this.type = type;
   }
   
   private List<Interceptor> interceptors;
   private Class<?> type;
   private transient WicketComponent component;
   private boolean callInProgress;
   private int reentrant = 0;
   
   private WicketComponent getComponent()
   {
      if (component == null)
      {
         component = WicketComponent.getInstance(type);
      }
      return component;
   }
   
   private List<Interceptor> getInterceptors()
   {
      if (interceptors ==  null)
      {
         interceptors = new ArrayList<Interceptor>();
         interceptors.add(new BijectionInterceptor());
      }
      return interceptors;
   }
   
   public void beforeInvoke(Object target, Method calledMethod)
   {
      doBeforeInvoke(new InvocationContext(calledMethod, target, getComponent()));
   }
   
   public Object afterInvoke(Object target, Method calledMethod, Object result)
   {
      return doAfterInvoke(new InvocationContext(calledMethod, target, getComponent()), result);
   }
   
   public void beforeInvoke(Object target, Constructor constructor)
   {
      getComponent().initialize(target);
      doBeforeInvoke(new InvocationContext(constructor, target, getComponent()));
   }
   
   public void afterInvoke(Object target, Constructor constructor)
   {
      doAfterInvoke(new InvocationContext(constructor, target, getComponent()), null);
   }
   
   private void doBeforeInvoke(InvocationContext invocationContext)
   {
      if (reentrant == 0)
      {
         for (Interceptor interceptor : getInterceptors())
         {
            interceptor.beforeInvoke(invocationContext);
         }
      }
      reentrant++;
   }
   
   public Exception handleException(Object target, Method method, Exception exception)
   {
      return doHandleException(new InvocationContext(method, target, getComponent()), exception);
   }
   
   public Exception handleException(Object target, Constructor constructor, Exception exception)
   {
      return doHandleException(new InvocationContext(constructor, target, getComponent()), exception);
   }
   
   private Exception doHandleException(InvocationContext invocationContext, Exception exception)
   {
      if (reentrant == 0)
      {
         for (Interceptor interceptor : getInterceptors())
         {
            exception = interceptor.handleException(invocationContext, exception);
         }
      }
      return exception;
   }
   
   private Object doAfterInvoke(InvocationContext invocationContext, Object result)
   {
      reentrant--;
      if (reentrant == 0)
      {
         for (int i = interceptors.size() - 1; i >= 0; i--)
         {
            result = interceptors.get(i).afterInvoke(invocationContext, result);
         }
      }
      return result;
   }

   public boolean isReentrant()
   {
      return reentrant > 0;
   }
   
   public static InstrumentedComponent getEnclosingInstance(Object bean, int level)
   {
      Class enclosingType = bean.getClass().getEnclosingClass();
      if (enclosingType != null)
      {
         try 
         {
            java.lang.reflect.Field enclosingField = bean.getClass().getDeclaredField("this$" + level);
            enclosingField.setAccessible(true);
            Object enclosingInstance = enclosingField.get(bean);
            if (enclosingInstance instanceof InstrumentedComponent)
            {
               return (InstrumentedComponent) enclosingInstance;
            }
         }
         catch (Exception e) 
         {
            if (level == 0)
            {
               return null;
            }
            else
            {
               return getEnclosingInstance(bean, level -1);
            }
         }
      }
      return null;
   }

}

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
   
   private List<RootInterceptor> interceptors;
   private Class<?> type;
   private transient WicketComponent component;
   private boolean callInProgress;
   
   private WicketComponent getComponent()
   {
      if (component == null)
      {
         component = WicketComponent.getInstance(type);
      }
      return component;
   }
   
   private List<RootInterceptor> getInterceptors()
   {
      if (interceptors ==  null)
      {
         interceptors = new ArrayList<RootInterceptor>();
         interceptors.add(new BijectionInterceptor());
      }
      return interceptors;
   }
   
   public void beforeInvoke(Object target, Method calledMethod)
   {
      beforeInvoke(new InvocationContext(calledMethod, target, getComponent()));
   }
   
   public void afterInvoke(Object target, Method calledMethod)
   {
      afterInvoke(new InvocationContext(calledMethod, target, getComponent()));
   }
   
   public void beforeInvoke(Object target)
   {
      getComponent().initialize(target);
      beforeInvoke(new InvocationContext(target, getComponent()));
   }
   
   public void afterInvoke(Object target)
   {
      afterInvoke(new InvocationContext(target, getComponent()));
   }
   
   private void beforeInvoke(InvocationContext invocationContext)
   {
      for (RootInterceptor interceptor : getInterceptors())
      {
         interceptor.beforeInvoke(invocationContext);
      }
   }
   
   private void afterInvoke(InvocationContext invocationContext)
   {
      for (RootInterceptor interceptor : getInterceptors())
      {
         interceptor.afterInvoke(invocationContext);
      }
   }
 
   public boolean isCallInProgress()
   {
      return callInProgress;
   }
   
   public void setCallInProgress(boolean callInProgress)
   {
      this.callInProgress = callInProgress;
   }
   
   public static InstrumentedComponent getEnclosingInstance(Object bean)
   {
      Class enclosingType = bean.getClass().getEnclosingClass();
      if (enclosingType != null)
      {
         try 
         {
            java.lang.reflect.Field enclosingField = bean.getClass().getDeclaredField("this$0");
            enclosingField.setAccessible(true);
            Object enclosingInstance = enclosingField.get(bean);
            if (enclosingInstance instanceof InstrumentedComponent)
            {
               return (InstrumentedComponent) enclosingInstance;
            }
         }
         catch (Exception e) {}
      }
      return null;
   }

}

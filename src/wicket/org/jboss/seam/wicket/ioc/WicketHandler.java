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
   
   public void init()
   {
      

   }
   
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
   
   public void beforeInvoke(Object target, Constructor constructor)
   {
      beforeInvoke(new InvocationContext(constructor, target, getComponent()));
   }
   
   public void afterInvoke(Object target, Constructor constructor)
   {
      afterInvoke(new InvocationContext(constructor, target, getComponent()));
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
      invocationContext.getComponent().initialize(invocationContext.getBean());
      for (RootInterceptor interceptor : getInterceptors())
      {
         interceptor.afterInvoke(invocationContext);
      }
   }
   
}

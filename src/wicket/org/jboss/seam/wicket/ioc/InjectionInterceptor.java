package org.jboss.seam.wicket.ioc;

import java.io.Serializable;
import java.lang.reflect.Method;

import javassist.util.proxy.MethodHandler;

import org.jboss.seam.annotations.In;
import org.jboss.seam.util.Reflections;

public abstract class InjectionInterceptor implements MethodHandler, Serializable
{
   private String name;
   private In annotation;

   public InjectionInterceptor(BijectedAttribute<In> in)
   {
      this.name = in.getName();
      this.annotation = in.getAnnotation();
   }

   public Object invoke(final Object proxy, final Method method, final Method proceed, final Object[] params) throws Throwable
   {
      if (!org.jboss.seam.web.Session.instance().isInvalid())
      {
         return Reflections.invoke(method, getValueToInject(name, annotation, proxy), params);
      }
      else
      {
         return null;
      }
   }
   
   protected abstract Object getValueToInject(String name, In annotation, Object value);
   
}

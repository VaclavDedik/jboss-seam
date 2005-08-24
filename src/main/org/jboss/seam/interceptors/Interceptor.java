//$Id$
package org.jboss.seam.interceptors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.ejb.AroundInvoke;
import javax.ejb.InvocationContext;

import org.jboss.seam.Component;
import org.jboss.seam.util.Reflections;

/**
 * Wraps and delegates to a Seam interceptor.
 * 
 * @author Gavin King
 */
public final class Interceptor extends Reflections
{
   private Method aroundInvokeMethod;
   private final Object userInterceptor;
   
   public Object getUserInterceptor()
   {
      return userInterceptor;
   }
   
   public String toString()
   {
      return "Interceptor(" + userInterceptor.getClass().getName() + ")";
   }
   
   public Interceptor(AbstractInterceptor builtinInterceptor, Component component)
   {
      userInterceptor = builtinInterceptor;
      init(null, component);
   }
   
   public Interceptor(Annotation annotation, Component component) 
   {
      javax.ejb.Interceptor interceptorAnnotation = annotation.annotationType()
            .getAnnotation(javax.ejb.Interceptor.class);
      try
      {
         userInterceptor = interceptorAnnotation.value().newInstance();
      }
      catch (Exception e)
      {
         throw new IllegalArgumentException(e);
      }
      init(annotation, component);
   }

   private void init(Annotation annotation, Component component)
   {
      for (Method method : userInterceptor.getClass().getMethods())
      {
         method.setAccessible(true);
         if ( method.isAnnotationPresent(AroundInvoke.class) )
         {
            aroundInvokeMethod = method;
         }
         Class[] params = method.getParameterTypes();
         if ( annotation!=null && params.length==1 && params[0]==annotation.annotationType() )
         {
            Reflections.invokeAndWrap(method, userInterceptor, annotation);
         }
         if ( params.length==1 && params[0]==Component.class )
         {
            Reflections.invokeAndWrap(method, userInterceptor, component);
         }
      }
      if (aroundInvokeMethod==null) 
      {
         throw new IllegalArgumentException("no @AroundInvoke method found");
      }
   }
   
   public Object aroundInvoke(InvocationContext invocation) throws Exception
   {
      return Reflections.invoke( aroundInvokeMethod, userInterceptor, invocation );
   }
}

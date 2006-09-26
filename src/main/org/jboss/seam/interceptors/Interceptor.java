//$Id$
package org.jboss.seam.interceptors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.PostActivate;
import javax.ejb.PrePassivate;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptors;
import javax.interceptor.InvocationContext;

import org.jboss.seam.Component;
import org.jboss.seam.InterceptorType;
import org.jboss.seam.util.Reflections;

/**
 * Wraps and delegates to a Seam interceptor.
 * 
 * @author Gavin King
 */
public final class Interceptor extends Reflections
{
   private final Object userInterceptor;
   private Method aroundInvokeMethod;
   private Method postConstructMethod;
   private Method preDestroyMethod;
   private Method postActivateMethod;
   private Method prePassivateMethod;
   private InterceptorType type;
   
   public Object getUserInterceptor()
   {
      return userInterceptor;
   }
   
   public InterceptorType getType()
   {
      return type;
   }
   
   public String toString()
   {
      return "Interceptor(" + userInterceptor.getClass().getName() + ")";
   }
   
   public Interceptor(Object interceptor, Component component)
   {
      userInterceptor = interceptor;
      init( null, component, interceptor.getClass() );
   }
   
   public Interceptor(Annotation annotation, Component component) 
   {
      Interceptors interceptorAnnotation = annotation.annotationType()
            .getAnnotation(Interceptors.class);
      Class interceptorClass;
      try
      {
         Class[] classes = interceptorAnnotation.value();
         if (classes.length!=1)
         {
            //TODO: remove this silly restriction!
            throw new IllegalArgumentException("Must be exactly one interceptor when used as a meta-annotation");
         }
         interceptorClass = classes[0];
         userInterceptor = interceptorClass.newInstance();
      }
      catch (Exception e)
      {
         throw new IllegalArgumentException("could not instantiate interceptor", e);
      }
      init(annotation, component, interceptorClass);
   }
   

   private void init(Annotation annotation, Component component, Class<?> interceptorClass)
   {
      for (Method method : userInterceptor.getClass().getMethods())
      {
         if ( !method.isAccessible() ) method.setAccessible(true);
         if ( method.isAnnotationPresent(AroundInvoke.class) )
         {
            aroundInvokeMethod = method;
         }
         if ( method.isAnnotationPresent(PostConstruct.class) )
         {
            postConstructMethod = method;
         }
         if ( method.isAnnotationPresent(PreDestroy.class) )
         {
            preDestroyMethod = method;
         }
         if ( method.isAnnotationPresent(PrePassivate.class) )
         {
            prePassivateMethod = method;
         }
         if ( method.isAnnotationPresent(PostActivate.class) )
         {
            postActivateMethod = method;
         }

         Class[] params = method.getParameterTypes();
         //if there is a method that takes the annotation, call it, to pass initialization info
         if ( annotation!=null && params.length==1 && params[0]==annotation.annotationType() )
         {
            Reflections.invokeAndWrap(method, userInterceptor, annotation);
         }
         //if there is a method that takes the component, call it
         if ( params.length==1 && params[0]==Component.class )
         {
            Reflections.invokeAndWrap(method, userInterceptor, component);
         }
      }

      type = interceptorClass.isAnnotationPresent(org.jboss.seam.annotations.Interceptor.class) ?
            interceptorClass.getAnnotation(org.jboss.seam.annotations.Interceptor.class).type() :
            InterceptorType.SERVER;
   }
   
   public Object aroundInvoke(InvocationContext invocation) throws Exception
   {
      return aroundInvokeMethod==null ?
            invocation.proceed() :
            Reflections.invoke( aroundInvokeMethod, userInterceptor, invocation );
   }
   public Object postConstruct(InvocationContext invocation) throws Exception
   {
      return postConstructMethod==null ?
            invocation.proceed() :
            Reflections.invoke( postConstructMethod, userInterceptor, invocation );
   }
   public Object preDestroy(InvocationContext invocation) throws Exception
   {
      return preDestroyMethod==null ?
            invocation.proceed() :
            Reflections.invoke( preDestroyMethod, userInterceptor, invocation );
   }
   public Object prePassivate(InvocationContext invocation) throws Exception
   {
      return prePassivateMethod==null ?
            invocation.proceed() :
            Reflections.invoke( prePassivateMethod, userInterceptor, invocation );
   }
   public Object postActivate(InvocationContext invocation) throws Exception
   {
      return postActivateMethod==null ?
            invocation.proceed() :
            Reflections.invoke( postActivateMethod, userInterceptor, invocation );
   }
   
}

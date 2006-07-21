//$Id$
package org.jboss.seam.util;

import java.beans.Introspector;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class Reflections
{
   public static Object invoke(Method method, Object target, Object... args) throws Exception
   {
      try
      {
         return method.invoke( target, args );
      }
      catch (IllegalArgumentException iae)
      {
         throw new IllegalArgumentException( 
               "Could not invoke method by reflection: " + toString(method) + 
               " with parameters: (" + Strings.toClassNameString(", ", args) + ')', 
               iae
            );
      }
      catch (InvocationTargetException ite)
      {
         if ( ite.getCause() instanceof Exception )
         {
            throw (Exception) ite.getCause();
         }
         else
         {
            throw ite;
         }
      }
   }
   
   public static Object invokeAndWrap(Method method, Object target, Object... args)
   {
      try
      {
         return invoke(method, target, args);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         if (e instanceof RuntimeException)
         {
            throw (RuntimeException) e;
         }
         else
         {
            throw new IllegalArgumentException("exception invoking: " + method.getName(), e);
         }
      }
   }
   
   private static String toString(Method method)
   {
      return Strings.unqualify( method.getClass().getName() ) + 
            '.' + 
            method.getName() + 
            '(' + 
            Strings.toString( ", ", method.getParameterTypes() ) + 
            ')';
   }
   
   public static Class classForName(String name) throws ClassNotFoundException
   {
      try 
      {
         return Thread.currentThread().getContextClassLoader().loadClass(name);
      }
      catch (Exception e)
      {
         return Class.forName(name);
      }
   }

   public static Class getCollectionElementType(Type collectionType)
   {
      if ( !(collectionType instanceof ParameterizedType) )
      {
         throw new IllegalArgumentException("collection type not parameterized");
      }
      Type[] typeArguments = ( (ParameterizedType) collectionType ).getActualTypeArguments();
      if (typeArguments.length==0)
      {
         throw new IllegalArgumentException("no type arguments for collection type");
      }
      Type typeArgument = typeArguments.length==1 ? typeArguments[0] : typeArguments[1]; //handle Maps
      if ( !(typeArgument instanceof Class) )
      {
         throw new IllegalArgumentException("type argument not a class");
      }
      return (Class) typeArgument;
   }
   
   public static Method getSetterMethod(Class clazz, String name)
   {
      Method[] methods = clazz.getMethods();
      for (Method method: methods)
      {
         String methodName = method.getName();
         if ( methodName.startsWith("set") && method.getParameterTypes().length==1 )
         {
            if ( Introspector.decapitalize( methodName.substring(3) ).equals(name) )
            {
               return method;
            }
         }
      }
      return null;
   }

}

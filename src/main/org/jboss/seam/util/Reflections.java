//$Id$
package org.jboss.seam.util;

import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
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
         String message = "Could not invoke method by reflection: " + toString(method);
         if (args!=null && args.length>0) 
         {
            message += " with parameters: (" + Strings.toClassNameString(", ", args) + ')';
         }
         message += " on: " + target.getClass().getName();
         throw new IllegalArgumentException(message, iae);
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
   
   public static Object get(Field field, Object target) throws Exception
   {
      try
      {
         return field.get(target);
      }
      catch (IllegalArgumentException iae)
      {
         String message = "Could not get field value by reflection: " + toString(field) + 
            " on: " + target.getClass().getName();
         throw new IllegalArgumentException(message, iae);
      }
   }
   
   public static void set(Field field, Object target, Object value) throws Exception
   {
      try
      {
         field.set(target, value);
      }
      catch (IllegalArgumentException iae)
      {
         String message = "Could not set field value by reflection: " + toString(field) +
            " on: " + target.getClass().getName();
         if (value==null)
         {
            message += " with null value";
         }
         else
         {
            message += " with value: " + value.getClass();
         }
         throw new IllegalArgumentException(message, iae);
      }
   }
   
   public static void setAndWrap(Field field, Object target, Object value)
   {
      try
      {
         set(field, target, value);
      }
      catch (Exception e)
      {
         if (e instanceof RuntimeException)
         {
            throw (RuntimeException) e;
         }
         else
         {
            throw new IllegalArgumentException("exception setting: " + field.getName(), e);
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
         if (e instanceof RuntimeException)
         {
            throw (RuntimeException) e;
         }
         else
         {
            throw new RuntimeException("exception invoking: " + method.getName(), e);
         }
      }
   }
   
   private static String toString(Method method)
   {
      return Strings.unqualify( method.getDeclaringClass().getName() ) + 
            '.' + 
            method.getName() + 
            '(' + 
            Strings.toString( ", ", method.getParameterTypes() ) + 
            ')';
   }
   
   private static String toString(Field field)
   {
      return Strings.unqualify( field.getDeclaringClass().getName() ) + 
            '.' + 
            field.getName();
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
   
   public static Class getMapKeyType(Type collectionType)
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
      Type typeArgument = typeArguments[0];
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
      throw new IllegalArgumentException("no such setter method: " + clazz.getName() + '.' + name);
   }
   
   public static Method getGetterMethod(Class clazz, String name)
   {
      Method[] methods = clazz.getMethods();
      for (Method method: methods)
      {
         String methodName = method.getName();
         if ( methodName.matches("^(get|is).*") && method.getParameterTypes().length==0 )
         {
            if ( Introspector.decapitalize( methodName.substring(3) ).equals(name) )
            {
               return method;
            }
         }
      }
      throw new IllegalArgumentException("no such getter method: " + clazz.getName() + '.' + name);
   }
   
   public static Field getField(Class clazz, String name)
   {
      for ( Class superClass = clazz; superClass!=Object.class; superClass=superClass.getSuperclass() )
      {
         try
         {
            return superClass.getDeclaredField(name);
         }
         catch (NoSuchFieldException nsfe) {}
      }
      throw new IllegalArgumentException("no such field: " + clazz.getName() + '.' + name);
   }

   public static Method getMethod(Annotation annotation, String name)
   {
      try
      {
         return annotation.annotationType().getMethod(name);
      }
      catch (NoSuchMethodException nsme)
      {
         return null;
      }
   }

}

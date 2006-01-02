//$Id$
package org.jboss.seam.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Reflections
{
   public static Object invoke(Method method, Object target, Object... args) throws Exception
   {
      try
      {
         return method.invoke( target, args );
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

}

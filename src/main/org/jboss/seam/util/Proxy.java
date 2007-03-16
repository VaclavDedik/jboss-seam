package org.jboss.seam.util;

public class Proxy
{
   
   public static Class deproxy(Class clazz)
   {
      if (org.jboss.seam.intercept.Proxy.class.isAssignableFrom(clazz))
      {
         return deproxy(clazz.getSuperclass());
      }
      else
      {
         return clazz;
      }
   }

}

/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.util;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.StringTokenizer;


/**
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public class Strings
{
   
   public static String unqualify(String name)
   {
      return name.substring( name.lastIndexOf('.')+1, name.length() );
   }
   
   public static boolean isEmptyOrNull(String string)
   {
      return string == null || string.trim().length() == 0; 
   }

   public static String toString(Object component)
   {
      try {
         PropertyDescriptor[] props = Introspector.getBeanInfo( component.getClass() )
               .getPropertyDescriptors();
         StringBuilder builder = new StringBuilder();
         for (PropertyDescriptor descriptor : props)
         {
            builder.append( descriptor.getName() )
               .append("=")
               .append( descriptor.getReadMethod().invoke(component) )
               .append("; ");
         }
         return builder.toString();
      }
      catch (Exception e) {
         return "";
      }
   }

   public static String[] split(String strings, String delims)
   {
      if (strings==null)
      {
         return new String[0];
      }
      else
      {      
         StringTokenizer tokens = new StringTokenizer(strings, delims);
         String[] result = new String[ tokens.countTokens() ];
         int i=0;
         while ( tokens.hasMoreTokens() )
         {
            result[i++] = tokens.nextToken();
         }
         return result;
      }
   }
   
   public static String toString(Object... objects)
   {
      return toString(" ", objects);
   }
   
   public static String toString(String sep, Object... objects)
   {
      StringBuilder builder = new StringBuilder();
      for (int i=0; i<objects.length; i++)
      {
         builder.append( objects[i].toString() );
         if (i<objects.length-1) builder.append(sep);
      }
      return builder.toString();
   }
   
   public static String toString(Class... classes)
   {
      StringBuilder builder = new StringBuilder();
      for (int i=0; i<classes.length; i++)
      {
         builder.append( classes[i].getName() );
         if (i<classes.length-1) builder.append(" ");
      }
      return builder.toString();
   }
}



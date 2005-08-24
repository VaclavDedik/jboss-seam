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
   
   public static String toString(Class... classes)
   {
      StringBuilder builder = new StringBuilder();
      for (Class clazz : classes)
      {
         builder.append( clazz.getName() ).append(" ");
      }
      return builder.toString();
   }
}



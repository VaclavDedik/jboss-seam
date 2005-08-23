/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.util;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;


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
}



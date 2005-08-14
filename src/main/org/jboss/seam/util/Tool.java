/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.util;


/**
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public class Tool
{
   public static String filenameToClassname(String filename)
   {
      return filename.substring(0, filename.lastIndexOf(".class"))
            .replace('/', '.').replace('\\', '.');
   }
   
   public static boolean isEmptyOrNull(String string)
   {
      return (string == null || string.trim().length() == 0); 
   }
}



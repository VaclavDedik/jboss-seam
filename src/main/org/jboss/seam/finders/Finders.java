//$Id$
package org.jboss.seam.finders;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Finders
{
   private static Map<Class, Finder> finders = new HashMap<Class, Finder>();
   static
   {
      finders.put(Properties.class, new PropertiesFinder());
   }
   
   public static Finder getFinder(Class clazz)
   {
      Finder finder = finders.get(clazz);
      return finder==null ? new ComponentFinder() : finder;
   }
}

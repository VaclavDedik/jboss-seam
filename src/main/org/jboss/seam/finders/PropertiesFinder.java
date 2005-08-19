//$Id$
package org.jboss.seam.finders;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Properties;

import org.jboss.seam.annotations.In;

/**
 * Loads properties
 * 
 * @author Gavin King
 */
public class PropertiesFinder implements Finder
{

   public String toName(In in, Method method)
   {
      String name = in.value();
      if (name==null || name.length() == 0)
      {
         name = method.getName().substring(3, 4).toLowerCase()
               + method.getName().substring(4)
               + ".properties";
      }
      return name;
   }

   public String toName(In in, Field field)
   {
      String name = in.value();
      if (name==null || name.length() == 0)
      {
         name = field.getName() + ".properties";
      }
      return name;
   }

   public Object find(In in, String name, Object bean)
   {
      Properties props = new Properties();
      try
      {
         props.load(bean.getClass().getResourceAsStream(name));
      } 
      catch (IOException ioe)
      {
         throw new RuntimeException(ioe);
      }
      return props;
   }

}

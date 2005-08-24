//$Id$
package org.jboss.seam.contexts;

import java.util.HashMap;
import java.util.Map;

public class EventContext implements Context
{
   
   private Map<String, Object> map = new HashMap<String, Object>();

   public Object get(Class clazz)
   {
      return null;
   }

   public Object get(String name)
   {
      return map.get(name);
   }

   public String[] getNames()
   {
      return map.keySet().toArray(new String[0]);
   }

   public boolean isSet(String name)
   {
      return map.containsKey(name);
   }

   public void remove(String name)
   {
      map.remove(name);
   }

   public void set(String name, Object value)
   {
      map.put(name, value);
   }

}

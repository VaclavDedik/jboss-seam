//$Id$
package org.jboss.seam.contexts;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.core.Events;

public class MapContext implements Context
{
   
   private Map<String, Object> map = new HashMap<String, Object>();
   private final ScopeType scope;
   
   public MapContext(ScopeType scope)
   {
      this.scope = scope;
   }

   public ScopeType getType()
   {
      return scope;
   }

   public Object get(Class clazz)
   {
      return get( Component.getComponentName(clazz) );
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
      if ( Events.exists() ) Events.instance().raiseEvent("org.jboss.seam.preRemoveVariable." + name);
      map.remove(name);
      if ( Events.exists() ) Events.instance().raiseEvent("org.jboss.seam.postRemoveVariable." + name);
   }

   public void set(String name, Object value)
   {
      if ( Events.exists() ) Events.instance().raiseEvent("org.jboss.seam.preSetVariable." + name);
      map.put(name, value);
      if ( Events.exists() ) Events.instance().raiseEvent("org.jboss.seam.postSetVariable." + name);
   }

   public void flush() {}

}

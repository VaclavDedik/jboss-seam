//$Id$
package org.jboss.seam.components;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("seamComponents")
@Scope(ScopeType.APPLICATION)
public class Components
{
   private Map<String, Component> components = new HashMap<String, Component>();
   
   public Component getComponent(String name)
   {
      return components.get(name);
   }
   
   public void addComponent(String className)
   {
      try
      {
         addComponent( Class.forName(className) );
      }
      catch (ClassNotFoundException e)
      {
         throw new IllegalArgumentException(e);
      }
   }
   
   public void addComponent(Class clazz)
   {
      addComponent( new Component(clazz) );
   }
   
   public void addComponent(Component component)
   {
      addComponent( component.getName(), component );
   }
   
   public void addComponent(String name, Component component)
   {
      Object old = components.put( name, component );
      if (old!=null)
      {
         throw new IllegalArgumentException("duplicate component name: " + name);
      }
   }
   
}

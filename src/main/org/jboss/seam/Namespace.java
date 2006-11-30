package org.jboss.seam;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Namespace extends AbstractMap<String, Object>
{
   
   public Namespace(String name) 
   {
      this.name = name;
   }
   
   private String name;
   private Map<String, Namespace> children = new HashMap<String, Namespace>();

   @Override
   public Set entrySet()
   {
      throw new UnsupportedOperationException();
   }
   
   @Override
   public Set<String> keySet()
   {
      throw new UnsupportedOperationException();
   }
   
   @Override
   public int size()
   {
      throw new UnsupportedOperationException();
   }
   
   @Override
   public Object get(Object key)
   {
      String qualifiedName = name==null ? key.toString() : name + key.toString();
      Object component = Component.getInstance(qualifiedName, true);
      return component==null ? children.get(key) : component;
   }
   
   public Namespace getChild(String key)
   {
      return children.get(key);
   }
   
   public boolean hasChild(String key)
   {
      return children.containsKey(key);
   }
   
   @Override
   public boolean containsKey(Object key)
   {
      return get(key)!=null;
   }
   
   @Override
   public boolean containsValue(Object value)
   {
      throw new UnsupportedOperationException();
   }
   
   @Override
   public void clear()
   {
      throw new UnsupportedOperationException();
   }
   
   @Override
   public Object remove(Object key)
   {
      throw new UnsupportedOperationException();
   }
   
   @Override
   public Collection<Object> values()
   {
      throw new UnsupportedOperationException();
   }
   
   @Override
   public void putAll(Map<? extends String, ? extends Object> t)
   {
      throw new UnsupportedOperationException();
   }

   public void addChild(String name, Namespace value)
   {
      children.put(name, value);
   }

   @Override
   public Object put(String name, Object value)
   {
      throw new UnsupportedOperationException();
   }
   
   @Override
   public int hashCode()
   {
      return name==null ? 0 : name.hashCode();
   }
   
   @Override
   public boolean equals(Object other)
   {
      if ( !(other instanceof Namespace) )
      {
         return false;
      }
      else
      {
         Namespace ns = (Namespace) other;
         return this.name==ns.name || 
               ( this.name!=null && this.name.equals(ns.name) );
      }
   }

}

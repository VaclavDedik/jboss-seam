package org.jboss.seam.ui.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.InterceptionType;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.AbstractMutable;
import org.jboss.seam.util.Proxy;

@Name("org.jboss.seam.ui.entityConverterStore")
@Scope(ScopeType.PAGE)
@Intercept(InterceptionType.NEVER)
public class EntityConverterStore extends AbstractMutable
{
   
   private class Key implements Serializable
   {
      private Class clazz;
      private Object id;
      
      public Key(Class clazz, Object id)
      {
         this.clazz = clazz;
         this.id = id;
      }
      
      public Class getClazz()
      {
         return clazz;
      }
      
      public Object getId()
      {
         return id;
      }
      
      
      @Override
      public boolean equals(Object other)
      {
         if (other instanceof Key)
         {
            Key that = (Key) other;
            if (id == null || clazz == null)
            {
               throw new IllegalArgumentException("Class and Id must not be null");
            }
            else 
            {
               return this.getId().equals(that.getId()) && this.getClazz().equals(that.getClazz());
            }
         }
         return false;
      }
   }

   private List<Key> store = new ArrayList<Key>();
   
   public Class getClass(Integer key)
   {
      return store.get(key).getClazz();
   }
   
   public Object getId(Integer key)
   {
      return store.get(key).getId();
   }
   
   public Integer put(Class clazz, Object id)
   {
      clazz = Proxy.deproxy(clazz);
      Key key = new Key(clazz, id);
      if (!store.contains(key))
      {
         store.add(key);
         setDirty();
         
      }
      return store.indexOf(key);
   }
   
   public static EntityConverterStore instance() 
   {
      if (!Contexts.isPageContextActive())
      {
         throw new IllegalArgumentException("Page scope not active");
      }
      return (EntityConverterStore) Component.getInstance(EntityConverterStore.class);
   }
   
}

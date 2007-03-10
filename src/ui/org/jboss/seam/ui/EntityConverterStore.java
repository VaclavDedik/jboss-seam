package org.jboss.seam.ui;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.InterceptionType;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;

@Name("entityConverterStore")
@Scope(ScopeType.SESSION)
@Intercept(InterceptionType.NEVER)
public class EntityConverterStore
{
   
   private class Key {
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
      Key key = new Key(clazz, id);
      if (!store.contains(key))
      {
         store.add(key);
         
      }
      return store.indexOf(key);
   }
   
   public static EntityConverterStore instance() 
   {
      if (!Contexts.isSessionContextActive())
      {
         throw new IllegalArgumentException("Session scope not active");
      }
      return (EntityConverterStore) Component.getInstance(EntityConverterStore.class);
   }
   
}

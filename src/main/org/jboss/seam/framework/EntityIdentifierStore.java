package org.jboss.seam.framework;

import static org.jboss.seam.annotations.Install.BUILT_IN;
import static org.jboss.seam.ScopeType.PAGE;
import static org.jboss.seam.InterceptionType.NEVER;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.Entity;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.persistence.PersistenceProvider;

/**
 * Stores entity identifiers under a key, which can be used on a page
 *
 */

@Name("org.jboss.seam.framework.entityIdentifierStore")
@Install(precedence=BUILT_IN)
@Scope(PAGE)
@Intercept(NEVER)
public class EntityIdentifierStore extends MutableEntityController
{
   
   public class Identifer implements Serializable
   {
      private Class clazz;
      private Object id;
      
      public Identifer(Object entity)
      {
         this(Entity.forClass(deproxy(entity.getClass())).getBeanClass(), PersistenceProvider.instance().getId(entity, getEntityManager()));
      }
      
      public Identifer(Class clazz, Object id)
      {
         if (clazz == null || id == null)
         {
            throw new IllegalArgumentException("Id and clazz must not be null");
         }
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
         if (other instanceof Identifer)
         {
            Identifer that = (Identifer) other;
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

   private List<Identifer> store = new ArrayList<Identifer>();
   
   /**
    * Load and return the entity stored
    * @param key
    * @return The entity or null if no entity is available at that key
    */
   public Object get(Integer key)
   {
      try
      {
         Identifer identifer = store.get(key);
         return find(identifer.getClazz(), identifer.getId());
      }
      catch (IndexOutOfBoundsException e)
      {
         return null;
      }   
      
   }
   
   /**
    * Store an entity id/clazz
    * @param entity The entity to store
    * @return The key under which the clazz/id are stored
    */
   public Integer put(Object entity)
   {      
      Identifer key = new Identifer(entity);
      if (!store.contains(key))
      {
         store.add(key);
         setDirty();
      }
      return store.indexOf(key);
   }
   
   public static EntityIdentifierStore instance() 
   {
      if (!Contexts.isPageContextActive())
      {
         throw new IllegalArgumentException("Page scope not active");
      }
      return (EntityIdentifierStore) Component.getInstance(EntityIdentifierStore.class);
   }
   
   // This maybe should be in Entity, not sure
   private static Class deproxy(Class clazz)
   {
      Class c = clazz;
      /* Work our way up the inheritance hierachy, looking of @Entity, if we are unsuccessful,
       * return the class we started with (possibly it's mapped in xml).
       * 
       * Workaround for lazy proxies and a lack of a way to do entityManager.getEntityClass(entity)
       */
      while (!Object.class.equals(c))
      {
         if (c.isAnnotationPresent(javax.persistence.Entity.class))
         {
              return c;
         }
         else
         {
            c = c.getSuperclass();
         }
      }
      return clazz;
   }
}

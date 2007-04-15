/**
 * 
 */
package org.jboss.seam.framework;

import javax.persistence.EntityManager;

import org.jboss.seam.Entity;
import org.jboss.seam.persistence.PersistenceProvider;

public class EntityIdentifier extends Identifier<EntityManager>
{
   public EntityIdentifier(Object entity, EntityManager entityManager)
   {
      super(Entity.forClass(deproxy(entity.getClass())).getBeanClass(), PersistenceProvider.instance().getId(entity, entityManager));
   }
   
   @Override
   public Object find(EntityManager entityManager)
   {
      entityManager.joinTransaction();
      return entityManager.find(getClazz(), getId());
   }
   
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
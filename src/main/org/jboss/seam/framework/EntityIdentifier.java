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
      super(Entity.forClass(entity.getClass()).getBeanClass(), PersistenceProvider.instance().getId(entity, entityManager));
   }
   
   @Override
   public Object find(EntityManager entityManager)
   {
      if (entityManager == null)
      {
         throw new IllegalArgumentException("EntityManager must not be null");
      }
      entityManager.joinTransaction();
      return entityManager.find(getClazz(), getId());
   }
   
}
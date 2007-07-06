package org.jboss.seam.framework;

import javax.persistence.EntityManager;
import javax.transaction.SystemException;

import org.jboss.seam.Entity;
import org.jboss.seam.persistence.PersistenceProvider;
import org.jboss.seam.transaction.Transaction;

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
      try
      {
         Transaction.instance().enlist(entityManager);
      }
      catch (SystemException se)
      {
         throw new RuntimeException("could not join transaction", se);
      }
      return entityManager.find(getClazz(), getId());
   }
   
}
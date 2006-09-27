package org.jboss.seam.framework;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.Transactional;

public class EntityActions
{
   private EntityManager entityManager;
   private Object entity;
   
   @Transactional
   public void persist()
   {
      entityManager.joinTransaction();
      entityManager.persist(entity);
   }

   @Transactional
   public void remove()
   {
      entityManager.joinTransaction();
      entityManager.remove(entity);
   }
   
   public boolean isManaged()
   {
      return entityManager.contains(entity);
   }

   public EntityManager getEntityManager()
   {
      return entityManager;
   }

   public void setEntityManager(EntityManager entityManager)
   {
      this.entityManager = entityManager;
   }

   public Object getEntity()
   {
      return entity;
   }

   public void setEntity(Object entity)
   {
      this.entity = entity;
   }
   
}

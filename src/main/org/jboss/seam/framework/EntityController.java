package org.jboss.seam.framework;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;

import org.jboss.seam.Component;

/**
 * Superclass for controller objects that perform
 * persistence operations using JPA. Adds
 * convenience methods for access to the JPA
 * EntityManager.
 * 
 * @author Gavin King
 *
 */
public class EntityController extends Controller
{
   private EntityManager entityManager;
   
   public EntityManager getEntityManager()
   {
      if (entityManager==null)
      {
         entityManager = (EntityManager) Component.getInstance("entityManager");
      }
      return entityManager;
   }
   
   public void setEntityManager(EntityManager entityManager)
   {
      this.entityManager = entityManager;
   }

   protected Query createNamedQuery(String name)
   {
      return entityManager.createNamedQuery(name);
   }

   protected Query createQuery(String ejbql)
   {
      return entityManager.createQuery(ejbql);
   }

   protected <T> T find(Class<T> clazz, Object id)
   {
      return entityManager.find(clazz, id);
   }

   protected void flush()
   {
      entityManager.flush();
   }

   protected <T> T getReference(Class<T> clazz, Object id)
   {
      return entityManager.getReference(clazz, id);
   }

   protected void lock(Object entity, LockModeType lockMode)
   {
      entityManager.lock(entity, lockMode);
   }

   protected <T> T merge(T entity)
   {
      return entityManager.merge(entity);
   }

   protected void persist(Object entity)
   {
      entityManager.persist(entity);
   }

   protected void refresh(Object entity)
   {
      entityManager.refresh(entity);
   }

   protected void remove(Object entity)
   {
      entityManager.remove(entity);
   }
   
}

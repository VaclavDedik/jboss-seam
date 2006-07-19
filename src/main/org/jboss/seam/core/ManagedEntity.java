package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Unwrap;

@Intercept(NEVER)
public class ManagedEntity
{
   private EntityManager entityManager;
   private Object id;
   private String entityClass;
   
   public EntityManager getEntityManager()
   {
      return entityManager;
   }

   public void setEntityManager(EntityManager entityManager)
   {
      this.entityManager = entityManager;
   }

   public Object getId()
   {
      return id;
   }

   public void setId(Object id)
   {
      this.id = id;
   }
   
   public String getEntityClass()
   {
      return entityClass;
   }

   public void setEntityClass(String entityClass)
   {
      this.entityClass = entityClass;
   }

   @Unwrap
   public Object getInstance() throws ClassNotFoundException
   {
      Class<?> clazz = Class.forName(entityClass);
      return entityManager.find(clazz, id);
   }

}

package org.jboss.seam.framework;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.FacesMessages;

public class EntityActions
{
   private EntityManager entityManager;
   private Object entity;
   
   @In(create=true) 
   FacesMessages facesMessages; 
   
   @Transactional
   public String update()
   {
      entityManager.joinTransaction();
      entityManager.flush();
      facesMessages.add("Successfully updated");
      return "updated";
   }
   
   @Transactional
   public String persist()
   {
      entityManager.joinTransaction();
      entityManager.persist(entity);
      entityManager.flush();
      facesMessages.add("Successfully created");
      return "persisted";
   }

   @Transactional
   public String remove()
   {
      entityManager.joinTransaction();
      entityManager.remove(entity);
      entityManager.flush();
      facesMessages.add("Successfully deleted");
      return "removed";
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

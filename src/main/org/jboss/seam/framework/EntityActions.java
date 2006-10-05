package org.jboss.seam.framework;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.FacesMessages;

public class EntityActions<E>
{
   private EntityManager entityManager;
   private E entity;
   
   @In(create=true) 
   private FacesMessages facesMessages; 
   
   @Transactional
   public String update()
   {
      getEntityManager().joinTransaction();
      getEntityManager().flush();
      facesMessages.add("Successfully updated");
      return "updated";
   }
   
   @Transactional
   public String persist()
   {
      getEntityManager().joinTransaction();
      getEntityManager().persist( getEntity() );
      getEntityManager().flush();
      facesMessages.add("Successfully created");
      return "persisted";
   }

   @Transactional
   public String remove()
   {
      getEntityManager().joinTransaction();
      getEntityManager().remove( getEntity() );
      getEntityManager().flush();
      facesMessages.add("Successfully deleted");
      return "removed";
   }
   
   public boolean isManaged()
   {
      return getEntityManager().contains( getEntity() );
   }

   public EntityManager getEntityManager()
   {
      return entityManager;
   }

   public void setEntityManager(EntityManager entityManager)
   {
      this.entityManager = entityManager;
   }

   public E getEntity()
   {
      return entity;
   }

   public void setEntity(E entity)
   {
      this.entity = entity;
   }
   
}

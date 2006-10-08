package org.jboss.seam.framework;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.util.Persistence;

public class EntityHome<E> extends Home<E>
{
   private EntityManager entityManager;

   @In(create=true) 
   private FacesMessages facesMessages; 
   
   @Transactional
   public boolean isManaged()
   {
      return getInstance()!=null && 
            getEntityManager().contains( getInstance() );
   }

   @Transactional
   public String update()
   {
      getEntityManager().joinTransaction();
      getEntityManager().flush();
      facesMessages.add( getUpdatedMessage() );
      return "updated";
   }
   
   @Transactional
   public String persist()
   {
      getEntityManager().joinTransaction();
      getEntityManager().persist( getInstance() );
      getEntityManager().flush();
      setId( Persistence.getId( getInstance(), getEntityManager() ) );
      facesMessages.add( getCreatedMessage() );
      return "persisted";
   }

   @Transactional
   public String remove()
   {
      getEntityManager().joinTransaction();
      getEntityManager().remove( getInstance() );
      getEntityManager().flush();
      facesMessages.add( getDeletedMessage() );
      return "removed";
   }
   
   @Transactional
   public E find()
   {
      getEntityManager().joinTransaction();
      E result = getEntityManager().find( getEntityClass(), getId() );
      if (result==null) result = handleNotFound();
      return result;
   }

   public EntityManager getEntityManager()
   {
      return entityManager;
   }

   public void setEntityManager(EntityManager entityManager)
   {
      this.entityManager = entityManager;
   }

}

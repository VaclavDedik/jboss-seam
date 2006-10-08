package org.jboss.seam.framework;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.util.Persistence;

public class EntityHome<E> extends Home<E>
{
   private EntityManager entityManager;
   
   private Object id;

   @In(create=true) 
   private FacesMessages facesMessages; 
   
   private String deletedMessage = "Successfully deleted";
   private String createdMessage = "Successfully created";
   private String updatedMessage = "Successfully updated";

   @Transactional
   public boolean isManaged()
   {
      return getEntityManager().contains( getInstance() );
   }

   @Transactional
   public String update()
   {
      getEntityManager().joinTransaction();
      getEntityManager().flush();
      facesMessages.add(updatedMessage);
      return "updated";
   }
   
   @Transactional
   public String persist()
   {
      getEntityManager().joinTransaction();
      getEntityManager().persist( getInstance() );
      getEntityManager().flush();
      setId( Persistence.getId( getInstance(), getEntityManager() ) );
      facesMessages.add(createdMessage);
      return "persisted";
   }

   @Transactional
   public String remove()
   {
      getEntityManager().joinTransaction();
      getEntityManager().remove( getInstance() );
      getEntityManager().flush();
      facesMessages.add(deletedMessage);
      return "removed";
   }
   
   @Transactional
   public E find(Object id)
   {
      getEntityManager().joinTransaction();
      E result = getEntityManager().find( getEntityClass(), id );
      if (result==null) result = handleNotFound();
      return result;
   }
   
   protected E find()
   {
      return find( getId() );
   }

   protected E handleNotFound()
   {
      throw new EntityNotFoundException();
   }

   @Override
   protected void initInstance()
   {
      if ( isIdDefined() )
      {
         //we cache the instance so that it does not "disappear"
         //after remove() is called on the instance
         //is this really a Good Idea??
         setInstance( find() );
      }
      else
      {
         super.initInstance();
      }            
   }

   public EntityManager getEntityManager()
   {
      return entityManager;
   }

   public void setEntityManager(EntityManager entityManager)
   {
      this.entityManager = entityManager;
   }

   public Class<E> getEntityClass()
   {
      return getObjectClass();
   }

   public void setEntityClass(Class<E> entityClass)
   {
      setObjectClass(entityClass);
   }
   
   public Object getId()
   {
      return id;
   }

   public void setId(Object id)
   {
      this.id = id;
   }
   
   public boolean isIdDefined()
   {
      return getId()!=null && !"".equals( getId() );
   }

   public String getCreatedMessage()
   {
      return createdMessage;
   }

   public void setCreatedMessage(String createdMessage)
   {
      this.createdMessage = createdMessage;
   }

   public String getDeletedMessage()
   {
      return deletedMessage;
   }

   public void setDeletedMessage(String deletedMessage)
   {
      this.deletedMessage = deletedMessage;
   }

   public String getUpdatedMessage()
   {
      return updatedMessage;
   }

   public void setUpdatedMessage(String updatedMessage)
   {
      this.updatedMessage = updatedMessage;
   }
   
}

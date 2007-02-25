package org.jboss.seam.framework;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.persistence.PersistenceProvider;

/**
 * Base class for Home objects of JPA entities.
 * 
 * @author Gavin King
 *
 */
public class EntityHome<E> extends Home<EntityManager, E>
{
   private static final long serialVersionUID = -3140094990727574632L;
   
   @Override
   public void create()
   {
      super.create();
      if ( getEntityManager()==null )
      {
         throw new IllegalStateException("entityManager is null");
      }
   }
   
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
      updatedMessage();
      return "updated";
   }
   
   @Transactional
   public String persist()
   {
      getEntityManager().joinTransaction();
      getEntityManager().persist( getInstance() );
      getEntityManager().flush();
      assignId( PersistenceProvider.instance().getId( getInstance(), getEntityManager() ) );
      createdMessage();
      return "persisted";
   }
   
   @Transactional
   public String remove()
   {
      getEntityManager().joinTransaction();
      getEntityManager().remove( getInstance() );
      getEntityManager().flush();
      deletedMessage();
      return "removed";
   }
   
   @Transactional
   @Override
   public E find()
   {
      getEntityManager().joinTransaction();
      E result = getEntityManager().find( getEntityClass(), getId() );
      if (result==null) result = handleNotFound();
      return result;
   }
   
   public EntityManager getEntityManager()
   {
      return getPersistenceContext();
   }
   
   public void setEntityManager(EntityManager entityManager)
   {
      setPersistenceContext(entityManager);
   }
   
   @Override
   protected String getPersistenceContextName()
   {
      return "entityManager";
   }
   
}

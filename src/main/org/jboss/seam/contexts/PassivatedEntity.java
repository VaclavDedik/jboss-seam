package org.jboss.seam.contexts;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.jboss.seam.Component;
import org.jboss.seam.Seam;
import org.jboss.seam.persistence.HibernatePersistenceProvider;
import org.jboss.seam.persistence.PersistenceContexts;
import org.jboss.seam.persistence.PersistenceProvider;
import org.jboss.seam.transaction.Transaction;

/**
 * A swizzled entity reference, consisting of the class,
 * id and persistence context name.
 * 
 * @see EntityBean
 * @see org.jboss.seam.interceptors.ManagedEntityIdentityInterceptor
 * 
 * @author Gavin King
 *
 */
class PassivatedEntity implements Serializable
{
   private static final long serialVersionUID = 6565440294007267788L;
   
   private Object id;
   private Object version;
   private String persistenceContext;
   private Class<?> entityClass; //TODO: make this transient, and serialize only the class name..
   
   private PassivatedEntity(Object id, Object version, Class<?> entityClass, String persistenceContext)
   {
      this.id = id;
      this.persistenceContext = persistenceContext;
      this.entityClass = entityClass;
      this.version = version;
   }
   
   public String getPersistenceContext()
   {
      return persistenceContext;
   }
   
   public Object getId()
   {
      return id;
   }
   
   public Class<?> getEntityClass()
   {
      return entityClass;
   }

   public Object toEntityReference()
   {
      Object persistenceContext = Component.getInstance( getPersistenceContext() );
      if ( persistenceContext==null )
      {
         return null;
      }
      else
      {
         if (persistenceContext instanceof EntityManager)
         {
            return getEntityFromEntityManager(persistenceContext);
         }
         else
         {
            return getEntityFromHibernate(persistenceContext);
         }
      }
   }

   private Object getEntityFromHibernate(Object persistenceContext)
   {
      //TODO: split this out to somewhere to isolate the Hibernate dependency!!
      Session session = (Session) persistenceContext;
      if ( session.isOpen() )
      {
         Object result = session.load( getEntityClass(), (Serializable) getId() );
         if (result!=null)
         {
            Object version = HibernatePersistenceProvider.getVersion(result, session);
            if (version!=null) 
            {
               HibernatePersistenceProvider.checkVersion(result, session, this.version, version);
            }
         }
         return result;
      }
      else
      {
         return null;
      }
   }

   private Object getEntityFromEntityManager(Object persistenceContext)
   {
      EntityManager em = (EntityManager) persistenceContext;
      if ( em.isOpen() )
      {
         Object result = em.getReference( getEntityClass(), getId() );
         if (result!=null)
         {
            Object version = PersistenceProvider.instance().getVersion(result, em);
            if (version!=null) 
            {
               PersistenceProvider.instance().checkVersion(result, em, this.version, version);
            }
         }
         return result;
      }
      else
      {
         return null;
      }
   }

   public static PassivatedEntity createPassivatedEntity(Object value)
   {
      Class entityClass = Seam.getEntityClass( value.getClass() );
      if (entityClass!=null)
      {
         for ( String persistenceContextName: PersistenceContexts.instance().getTouchedContexts() )
         {
            Object persistenceContext = Component.getInstance(persistenceContextName);
            PassivatedEntity result;
            if (persistenceContext instanceof EntityManager)
            {
               result = createUsingEntityManager(value, entityClass, persistenceContextName, persistenceContext);
            }
            else
            {
               result = createUsingHibernate(value, entityClass, persistenceContextName, persistenceContext);
            }
            if (result!=null) return result;
         }
      }
      return null;
   }

   private static PassivatedEntity createUsingHibernate(Object value, Class entityClass, String persistenceContextName, Object persistenceContext)
   {
      //TODO: split this out to somewhere to isolate the Hibernate dependency!!
      Session session = (Session) persistenceContext;
      if ( isManaged(value, session) )
      {
         Object id = session.getIdentifier(value);
         Object version = HibernatePersistenceProvider.getVersion(value, session);
         return create(entityClass, persistenceContextName, id, version);
      }
      else
      {
         return null;
      }
   }

   private static boolean isManaged(Object value, Session session)
   {
      boolean managed;
      try
      {
         managed = session.isOpen() && session.contains(value);
      }
      catch (RuntimeException re) 
      {
         //just in case! //TODO; deleteme
         managed = false;
      }
      return managed;
   }

   private static PassivatedEntity createUsingEntityManager(Object value, Class entityClass, String persistenceContextName, Object persistenceContext)
   {
      EntityManager em = (EntityManager) persistenceContext;
      if ( isManaged(value, em) )
      {
         Object id = PersistenceProvider.instance().getId(value, em);
         Object version = PersistenceProvider.instance().getVersion(value, em);
         return create(entityClass, persistenceContextName, id, version);
      }
      else
      {
         return null;
      }
   }

   private static boolean isManaged(Object value, EntityManager em)
   {
      boolean managed;
      try
      {
         managed = em.isOpen() && em.contains(value);
      }
      catch (RuntimeException re) 
      {
         //workaround for bug in HEM! //TODO; deleteme
         managed = false;
      }
      return managed;
   }

   private static PassivatedEntity create(Class entityClass, String persistenceContextName, Object id, Object version)
   {
      if (id==null)
      {
         //this can happen if persist() fails in Hibernate
         return null;
      }
      else
      {
         return new PassivatedEntity(id, version, entityClass, persistenceContextName);
      }
   }

   static boolean isTransactionRolledBackOrMarkedRollback()
   {
      try
      {
         return Transaction.instance().isRolledBackOrMarkedRollback();
      }
      catch (Exception e)
      {
         return false;
      }
   }
   
   @Override
   public String toString()
   {
      return entityClass + "#" + id;
   }
   
}
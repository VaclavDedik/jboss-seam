package org.jboss.seam.contexts;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;

import org.hibernate.EntityMode;
import org.hibernate.Session;
import org.hibernate.metadata.ClassMetadata;
import org.jboss.seam.Component;
import org.jboss.seam.Seam;
import org.jboss.seam.core.PersistenceContexts;
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
public class PassivatedEntity implements Serializable
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
         Object result = null;
         Object version = null;
         if (persistenceContext instanceof EntityManager)
         {
            EntityManager em = (EntityManager) persistenceContext;
            if ( em.isOpen() )
            {
               result = em.getReference( getEntityClass(), getId() );
               if (result!=null)
               {
                  version = PersistenceProvider.instance().getVersion(result, em);
               }
            }
         }
         else
         {
            //TODO: split this out to somewhere to isolate the Hibernate dependency!!
            Session session = (Session) persistenceContext;
            if ( session.isOpen() )
            {
               result = session.load( getEntityClass(), (Serializable) getId() );
               if (result!=null)
               {
                  version = getVersion(result, session);
               }
            }
         }
         if ( result!=null && this.version!=null && !this.version.equals(version) )
         {
            throw new OptimisticLockException("current database version number does not match passivated version number");
         }
         return result;
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
            boolean managed;
            Object id = null;
            Object version = null;
            if (persistenceContext instanceof EntityManager)
            {
               EntityManager em = (EntityManager) persistenceContext;
               try
               {
                  managed = em.isOpen() && em.contains(value);
               }
               catch (RuntimeException re) 
               {
                  //workaround for bug in HEM! //TODO; deleteme
                  managed = false;
               }
               if (managed)
               {
                  id = PersistenceProvider.instance().getId(value, em);
                  version = PersistenceProvider.instance().getVersion(value, em);
               }
            }
            else
            {
               //TODO: split this out to somewhere to isolate the Hibernate dependency!!
               Session session = (Session) persistenceContext;
               try
               {
                  managed = session.isOpen() && session.contains(value);
               }
               catch (RuntimeException re) 
               {
                  //just in case! //TODO; deleteme
                  managed = false;
               }
               if (managed)
               {
                  id = session.getIdentifier(value);
                  version = getVersion(value, session);
               }
            }
            if (managed)
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
         }
      }
      return null;
   }

   private static Object getVersion(Object value, Session session)
   {
      ClassMetadata classMetadata = session.getSessionFactory()
                  .getClassMetadata( value.getClass() );
      return classMetadata.isVersioned() ? 
               classMetadata.getVersion(value, EntityMode.POJO) : null;
   }
   
   public static boolean isTransactionRolledBackOrMarkedRollback()
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
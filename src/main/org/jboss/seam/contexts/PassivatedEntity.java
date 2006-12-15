/**
 * 
 */
package org.jboss.seam.contexts;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.jboss.seam.Component;
import org.jboss.seam.Seam;
import org.jboss.seam.core.PersistenceContexts;
import org.jboss.seam.persistence.PersistenceProvider;
import org.jboss.seam.util.Transactions;

public class PassivatedEntity implements Serializable
{
   
   private Object id;
   private String persistenceContext;
   private String fieldName;
   private Class<?> entityClass;
   
   private PassivatedEntity(Object id, Class<?> entityClass, String persistenceContext, String fieldName)
   {
      this.id = id;
      this.persistenceContext = persistenceContext;
      this.fieldName = fieldName;
      this.entityClass = entityClass;
   }
   
   public String getPersistenceContext()
   {
      return persistenceContext;
   }
   
   public Object getId()
   {
      return id;
   }
   
   public String getFieldName()
   {
      return fieldName;
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
            EntityManager em = (EntityManager) persistenceContext;
            return em.isOpen() ? 
                     em.getReference( getEntityClass(), getId() ) : null;
         }
         else
         {
            Session session = (Session) persistenceContext;
            return session.isOpen() ? 
                     session.load( getEntityClass(), (Serializable) getId() ) : null;
         }
      }
   }

   public static PassivatedEntity createPassivatedEntity(Object value, String fieldName)
   {
      Class entityClass = Seam.getEntityClass( value.getClass() );
      if (entityClass!=null)
      {
         for ( String persistenceContextName: PersistenceContexts.instance().getTouchedContexts() )
         {
            Object persistenceContext = Component.getInstance(persistenceContextName);
            boolean managed;
            Object id;
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
               id = managed ? PersistenceProvider.instance().getId(value, em) : null;
            }
            else
            {
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
               id = managed ? session.getIdentifier(value) : null;
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
                  return new PassivatedEntity( id, entityClass, persistenceContextName, fieldName );
               }
            }
         }
      }
      return null;
   }
   
   public static boolean isTransactionMarkedRollback()
   {
      try
      {
         return Transactions.isTransactionMarkedRollback();
      }
      catch (Exception e)
      {
         return false;
      }
   }
   
}
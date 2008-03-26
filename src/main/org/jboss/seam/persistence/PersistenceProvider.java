package org.jboss.seam.persistence;
import static org.jboss.seam.annotations.Install.BUILT_IN;
import static org.jboss.seam.util.Reflections.isInstanceOf;

import java.lang.reflect.Method;

import javax.persistence.EntityManager;
import javax.transaction.Synchronization;

import org.jboss.seam.Component;
import org.jboss.seam.Entity;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
/**
 * Abstraction layer for persistence providers (JPA implementations).
 * This class delegates to either the generic JpaPersistenceProvider or a more
 * specific one if available.
 * 
 * The methods on this class are a great todo list for the next rev
 * of the JPA spec ;-)
 * 
 * @author Gavin King
 * @author Pete Muir
 *
 */
@Name("org.jboss.seam.persistence.persistenceProvider")
@Scope(ScopeType.STATELESS)
@BypassInterceptors
@Install(precedence=BUILT_IN, classDependencies="javax.persistence.EntityManager")
public class PersistenceProvider
{
   /**
    *  Set the flush mode to manual-only flushing. Called when
    *  an atomic persistence context is required.
    */
   public void setFlushModeManual(EntityManager entityManager)
   {
      getPersistenceProvider(entityManager).setFlushModeManual(entityManager);
   }
   /**
    * Does the persistence context have unflushed changes? If
    * it does not, persistence context replication can be
    * optimized.
    * 
    * @return true to indicate that there are unflushed changes
    */
   public boolean isDirty(EntityManager entityManager)
   {
      return getPersistenceProvider(entityManager).isDirty(entityManager);
   }
   
   /**
    * Get the value of the entity identifier attribute.
    * 
    * @param bean a managed entity instance
    */
   public Object getId(Object bean, EntityManager entityManager)
   {
      return getPersistenceProvider(entityManager).getId(bean, entityManager);
   }
   
   /**
    * Get the name of the entity
    * 
    * @param bean
    * @param entityManager
    * 
    * @throws IllegalArgumentException if the passed object is not an entity
    */
   public String getName(Object bean, EntityManager entityManager) throws IllegalArgumentException
   {
      return getPersistenceProvider(entityManager).getName(bean, entityManager);
   }
   
   /**
    * Get the value of the entity version attribute.
    * 
    * @param bean a managed entity instance
    */
   public Object getVersion(Object bean, EntityManager entityManager)
   {
      return getPersistenceProvider(entityManager).getVersion(bean, entityManager);
   }
   
   public void checkVersion(Object bean, EntityManager entityManager, Object oldVersion, Object version)
   {
      getPersistenceProvider(entityManager).checkVersion(bean, entityManager, oldVersion, version);
   }
   /**
    * Enable a Filter. This is here just especially for Hibernate,
    * since we well know that other products don't have such cool
    * features. 
    */
   public void enableFilter(Filter filter, EntityManager entityManager)
   {
      getPersistenceProvider(entityManager).enableFilter(filter, entityManager);
   }
   
   /**
    * Register a Synchronization with the current transaction.
    */
   public boolean registerSynchronization(Synchronization sync, EntityManager entityManager)
   {
      return getPersistenceProvider(entityManager).registerSynchronization(sync, entityManager);
   }
   
   public static PersistenceProvider instance()
   {
      return (PersistenceProvider) Component.getInstance(PersistenceProvider.class, ScopeType.STATELESS);
   }

   /**
    * Wrap the delegate before returning it to the application
    */
   @Deprecated
   public Object proxyDelegate(Object delegate)
   {
      return getPersistenceProvider(delegate).proxyDelegate(delegate);
   }
   
   public Object proxyDelegate(EntityManager entityManager, Object delegate)
   {
      return getPersistenceProvider(entityManager).proxyDelegate(delegate);
   }
   
   /**
    * Wrap the entityManager before returning it to the application
    */
   public EntityManager proxyEntityManager(EntityManager entityManager) {
      return getPersistenceProvider(entityManager).proxyEntityManager(entityManager);
   }
   
   /**
    * Returns the class of an entity bean instance
    * 
    * @param bean The entity bean instance
    * @return The class of the entity bean
    */
   @Deprecated
   public Class getBeanClass(Object bean)
   {
      return Entity.forClass(bean.getClass()).getBeanClass();
   }
   
   public Class getBeanClass(EntityManager entityManager, Object bean)
   {
      return getPersistenceProvider(entityManager).getBeanClass(bean);
   }
   
   public Method getPostLoadMethod(Class beanClass, EntityManager entityManager)
   {
      return getPersistenceProvider(entityManager).getPostLoadMethod(beanClass, entityManager);     
   }
   
   public Method getPrePersistMethod(Class beanClass, EntityManager entityManager)
   {
      return getPersistenceProvider(entityManager).getPrePersistMethod(beanClass, entityManager);
   }
   
   public Method getPreUpdateMethod(Class beanClass, EntityManager entityManager)
   {
      return getPersistenceProvider(entityManager).getPreUpdateMethod(beanClass, entityManager);
   }
   
   public Method getPreRemoveMethod(Class beanClass, EntityManager entityManager)
   {
      return getPersistenceProvider(entityManager).getPreRemoveMethod(beanClass, entityManager);
   }
   
   /**
    * Do runtime detection of PersistenceProvider
    */
   private AbstractPersistenceProvider getPersistenceProvider(EntityManager entityManager)
   {
      // Work around EJBTHREE-912 (don't you just love random NPEs!)
      if (isInstanceOf(entityManager.getClass(), "org.jboss.ejb3.entity.HibernateSession"))
      {
         return HibernatePersistenceProvider.instance();
      }
      else if(isInstanceOf(entityManager.getDelegate().getClass(), "org.hibernate.Session"))
      {
         return HibernatePersistenceProvider.instance();
      }
      else
      {
         return JpaPersistenceProvider.instance();
      }
   }
   
   /**
    * Do runtime detection of PersistenceProvider
    */
   @Deprecated
   private AbstractPersistenceProvider getPersistenceProvider(Object delegate)
   {
      if (isInstanceOf(delegate.getClass(), "org.hibernate.Session"))
      {
         return HibernatePersistenceProvider.instance();
      }
      else
      {
         return JpaPersistenceProvider.instance();
      }
   }
   
}

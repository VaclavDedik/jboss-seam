package org.jboss.seam.persistence;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.lang.reflect.Method;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.transaction.Synchronization;

import org.jboss.seam.Component;
import org.jboss.seam.ComponentType;
import org.jboss.seam.Entity;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.init.EjbDescriptor;
/**
 * Abstraction layer for persistence providers (JPA implementations).
 * This class provides a working base implementation that can be
 * optimized for performance and non-standardized features by extending
 * and overriding the methods. 
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
      throw new UnsupportedOperationException("For use of FlushMode.MANUAL, please use Hibernate as the persistence provider or use a custom PersistenceProvider");
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
      return true; //best we can do!
   }
   
   /**
    * Get the value of the entity identifier attribute.
    * 
    * @param bean a managed entity instance
    */
   public Object getId(Object bean, EntityManager entityManager)
   {
      return Entity.forBean( bean ).getIdentifier(bean);
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
      return Entity.forBean( bean ).getName();
   }
   
   /**
    * Get the value of the entity version attribute.
    * 
    * @param bean a managed entity instance
    */
   public Object getVersion(Object bean, EntityManager entityManager)
   {
      return Entity.forBean( bean ).getVersion(bean);
   }
   
   public void checkVersion(Object bean, EntityManager entityManager, Object oldVersion, Object version)
   {
      boolean equal;
      if (oldVersion instanceof Date)
      {
         equal = ( (Date) oldVersion ).getTime() == ( (Date) version ).getTime();
      }
      else
      {
         equal = oldVersion.equals(version);
      }
      if ( !equal )
      {
         throw new OptimisticLockException("current database version number does not match passivated version number");
      }
   }
   /**
    * Enable a Filter. This is here just especially for Hibernate,
    * since we well know that other products don't have such cool
    * features. 
    */
   public void enableFilter(Filter filter, EntityManager entityManager)
   {
      throw new UnsupportedOperationException("For filters, please use Hibernate as the persistence provider");
   }
   
   /**
    * Register a Synchronization with the current transaction.
    */
   public boolean registerSynchronization(Synchronization sync, EntityManager entityManager)
   {
      return false; //best we can do!
   }
   
   public static PersistenceProvider instance()
   {
      return (PersistenceProvider) Component.getInstance(PersistenceProvider.class, ScopeType.STATELESS);
   }

   /**
    * Wrap the delegate before returning it to the application
    */
   public Object proxyDelegate(Object delegate)
   {
      return delegate;
   }
   /**
    * Wrap the entityManager before returning it to the application
    */
   public EntityManager proxyEntityManager(EntityManager entityManager) {
      return new EntityManagerProxy(entityManager);
   }
   
   /**
    * Returns the class of an entity bean instance
    * 
    * @param bean The entity bean instance
    * @return The class of the entity bean
    */
   public Class getBeanClass(Object bean)
   {
      return getEntityClass(bean.getClass());
   }
   
   public static Class getEntityClass(Class clazz)
   {
      while (clazz != null && !Object.class.equals(clazz))
      {
         if (clazz.isAnnotationPresent(Entity.class))
         {
            return clazz;
         }
         else
         {
            EjbDescriptor ejbDescriptor = Seam.getEjbDescriptor(clazz.getName());
            if (ejbDescriptor != null)
            {
               return ejbDescriptor.getBeanType() == ComponentType.ENTITY_BEAN ? clazz : null;
            }
            else
            {
               clazz = clazz.getSuperclass();
            }
         }
      }
      return null;
   }
   
   public Method getPostLoadMethod(Object bean, EntityManager entityManager)
   {
      return Entity.forBean(bean).getPostLoadMethod();
   }
   
   public Method getPrePersistMethod(Object bean, EntityManager entityManager)
   {
      return Entity.forBean(bean).getPrePersistMethod();
   }
   
   public Method getPreUpdateMethod(Object bean, EntityManager entityManager)
   {
      return Entity.forBean(bean).getPreUpdateMethod();
   }
   
   public Method getPreRemoveMethod(Object bean, EntityManager entityManager)
   {
      return Entity.forBean(bean).getPreRemoveMethod();
   }
   
   @Deprecated
   public Method getPreRemoveMethod(Class beanClass)
   {
      return Entity.forClass(beanClass).getPreRemoveMethod();
   }
   
   @Deprecated
   public Method getPostLoadMethod(Class beanClass)
   {
      return Entity.forClass(beanClass).getPostLoadMethod();      
   }
   
   @Deprecated
   public Method getPrePersistMethod(Class beanClass)
   {
      return Entity.forClass(beanClass).getPrePersistMethod();
   }
   
   @Deprecated
   public Method getPreUpdateMethod(Class beanClass)
   {
      return Entity.forClass(beanClass).getPreUpdateMethod();
   }
   

   
}

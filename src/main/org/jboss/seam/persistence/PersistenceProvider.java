package org.jboss.seam.persistence;

import static org.jboss.seam.annotations.Install.BUILT_IN;

import javax.persistence.EntityManager;
import javax.transaction.Synchronization;

import org.jboss.seam.Component;
import org.jboss.seam.Entity;
import org.jboss.seam.InterceptionType;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Filter;
import org.jboss.seam.core.ManagedPersistenceContext;

/**
 * Abstraction layer for persistence providers (JPA implementations).
 * This class provides a working base implementation that can be
 * optimized for performance and non-standardized features by extending
 * and overriding the methods. 
 * 
 * @author Gavin King
 *
 */
@Name("org.jboss.seam.persistence.persistenceProvider")
@Scope(ScopeType.STATELESS)
@Intercept(InterceptionType.NEVER)
@Install(precedence=BUILT_IN, genericDependencies=ManagedPersistenceContext.class)
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
      return Entity.forClass( bean.getClass() ).getIdentifier(bean);
   }
   
   /**
    * Get the value of the entity version attribute.
    * 
    * @param bean a managed entity instance
    */
   public Object getVersion(Object bean, EntityManager entityManager)
   {
      return Entity.forClass( bean.getClass() ).getVersion(bean);
   }
   
   /**
    * Enable a Filter. This is here just especially for Hibernate,
    * since we well know that other products don't have such cool
    * features. 
    */
   public void enableFilter(Filter f, EntityManager entityManager)
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
    * What flush policy should we use during the render response phase?
    * We should not be changing data during the render, so we can 
    * optimize performance by choosing not to flush.
    * 
    * @return COMMIT or MANUAL
    */
   public FlushModeType getRenderFlushMode()
   {
      return FlushModeType.COMMIT;
   }

}

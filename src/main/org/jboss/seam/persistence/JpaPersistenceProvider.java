package org.jboss.seam.persistence;

import static org.jboss.seam.ScopeType.STATELESS;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import javax.persistence.EntityManager;
import javax.transaction.Synchronization;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

/**
 * PersistenceProvider for any JPA implementation. Many methods are unusable
 * or use naive implementations
 * 
 * @author Gavin King
 * @author Pete Muir
 *
 */
@Name("org.jboss.seam.persistence.jpaPersistenceProvider")
@Scope(STATELESS)
@BypassInterceptors
@Install(precedence=BUILT_IN, classDependencies="javax.persistence.EntityManager")
public class JpaPersistenceProvider extends AbstractPersistenceProvider
{

   @Override
   public void enableFilter(Filter filter, EntityManager entityManager)
   {
      throw new UnsupportedOperationException("You must use Hibernate to use Filters");
   }

   @Override
   public boolean isDirty(EntityManager entityManager)
   {
      return true;
   }

   @Override
   public boolean registerSynchronization(Synchronization sync, EntityManager entityManager)
   {
      return false;
   }

   @Override
   public void setFlushModeManual(EntityManager entityManager)
   {
      throw new UnsupportedOperationException("You must use Hibernate to use Manual Flush Mode");
   }
   
   public static JpaPersistenceProvider instance()
   {
      return (JpaPersistenceProvider) Component.getInstance(JpaPersistenceProvider.class, STATELESS);
   }
   
}

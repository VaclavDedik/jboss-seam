package org.jboss.seam.persistence;

import static org.jboss.seam.annotations.Install.BUILT_IN;

import javax.persistence.EntityManager;

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

   public void setFlushModeManual(EntityManager entityManager)
   {
      throw new UnsupportedOperationException("For use of FlushMode.MANUAL, please use Hibernate as the persistence provider or use a custom PersistenceProvider");
   }

   public boolean isDirty(EntityManager entityManager)
   {
      return true; //best we can do!
   }
   public Object getId(Object bean, EntityManager entityManager)
   {
      return Entity.forClass( bean.getClass() ).getIdentifier(bean);
   }
   
   public void enableFilter(Filter f, EntityManager entityManager)
   {
      throw new UnsupportedOperationException("For filters, please use Hibernate as the persistence provider");
   }
   
   public static PersistenceProvider instance()
   {
      return (PersistenceProvider) Component.getInstance(PersistenceProvider.class, ScopeType.STATELESS);
   }

   public FlushModeType getRenderFlushMode()
   {
      return FlushModeType.COMMIT;
   }

}

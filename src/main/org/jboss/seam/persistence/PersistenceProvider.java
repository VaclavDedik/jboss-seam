package org.jboss.seam.persistence;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.InterceptionType;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("org.jboss.seam.persistence.persistenceProvider")
@Scope(ScopeType.STATELESS)
@Intercept(InterceptionType.NEVER)
public class PersistenceProvider
{

   public void setFlushModeManual(EntityManager entityManager)
   {
      throw new IllegalArgumentException("For use of FlushMode.MANUAL, please use Hibernate as the persistence provider or provide a custom PersistenceProvider");
   }

   public boolean isDirty(EntityManager entityManager)
   {
      return true; //best we can do!
   }

   public Object getId(Object bean, EntityManager entityManager) 
   {
      throw new IllegalArgumentException("For Seam-managed persistence contexts, please use Hibernate as the persistence provider or provide a custom PersistenceProvider");
   }
   
   public static PersistenceProvider instance()
   {
      return (PersistenceProvider) Component.getInstance(PersistenceProvider.class, ScopeType.STATELESS, true);
   }

}

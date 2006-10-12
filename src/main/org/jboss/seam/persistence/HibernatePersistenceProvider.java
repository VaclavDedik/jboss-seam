package org.jboss.seam.persistence;

import javax.persistence.EntityManager;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.jboss.seam.InterceptionType;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("org.jboss.seam.persistence.persistenceProvider")
@Scope(ScopeType.STATELESS)
@Intercept(InterceptionType.NEVER)
public class HibernatePersistenceProvider extends PersistenceProvider
{

   public void setFlushModeManual(EntityManager entityManager)
   {
      ( (Session) entityManager.getDelegate() ).setFlushMode(FlushMode.NEVER);
   }

   public boolean isDirty(EntityManager entityManager)
   {
      return ( (Session) entityManager.getDelegate() ).isDirty();
   }

   public Object getId(Object bean, EntityManager entityManager) 
   {
      return ( (Session) entityManager.getDelegate() ).getIdentifier(bean);
   }

}

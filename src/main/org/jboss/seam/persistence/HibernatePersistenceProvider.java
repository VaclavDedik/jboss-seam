package org.jboss.seam.persistence;

import static org.jboss.seam.annotations.Install.FRAMEWORK;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.transaction.Synchronization;

import org.hibernate.EntityMode;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.metadata.ClassMetadata;
import org.jboss.seam.InterceptionType;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Filter;
import org.jboss.seam.core.ManagedPersistenceContext;
import org.jboss.seam.core.Expressions.ValueExpression;

/**
 * Support for non-standardized features of Hibernate, when
 * used as the JPA persistence provider.
 * 
 * @author Gavin King
 *
 */
@Name("org.jboss.seam.persistence.persistenceProvider")
@Scope(ScopeType.STATELESS)
@Intercept(InterceptionType.NEVER)
@Install(precedence=FRAMEWORK, classDependencies="org.hibernate.Session", genericDependencies=ManagedPersistenceContext.class)
public class HibernatePersistenceProvider extends PersistenceProvider
{

   @Override
   public void setFlushModeManual(EntityManager entityManager)
   {
      getSession(entityManager).setFlushMode(FlushMode.NEVER);
   }

   @Override
   public boolean isDirty(EntityManager entityManager)
   {
      return getSession(entityManager).isDirty();
   }

   @Override
   public Object getId(Object bean, EntityManager entityManager) 
   {
      return getSession(entityManager).getIdentifier(bean);
   }

   @Override
   public Object getVersion(Object bean, EntityManager entityManager) 
   {
      ClassMetadata classMetadata = getSession(entityManager).getSessionFactory()
                  .getClassMetadata( bean.getClass() );
      return classMetadata.isVersioned() ? 
               classMetadata.getVersion(bean, EntityMode.POJO) : null;
   }

   @Override
   public void enableFilter(Filter f, EntityManager entityManager)
   {
      org.hibernate.Filter filter = getSession(entityManager).enableFilter( f.getName() );
      for ( Map.Entry<String, ValueExpression> me: f.getParameters().entrySet() )
      {
         filter.setParameter( me.getKey(), me.getValue().getValue() );
      }
      filter.validate();
   }
   
   @Override
   public boolean registerSynchronization(Synchronization sync, EntityManager entityManager)
   {
      getSession(entityManager).getTransaction().registerSynchronization(sync);
      return true;
   }
   
   @Override
   public FlushModeType getRenderFlushMode()
   {
      return FlushModeType.MANUAL;
   }
   
   private Session getSession(EntityManager entityManager)
   {
      return (Session) entityManager.getDelegate();
   }

}

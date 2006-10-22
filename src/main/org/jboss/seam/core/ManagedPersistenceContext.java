//$Id$
package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionEvent;
import javax.transaction.SystemException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.Expressions.ValueBinding;
import org.jboss.seam.persistence.PersistenceProvider;
import org.jboss.seam.util.Naming;
import org.jboss.seam.util.Transactions;

/**
 * A Seam component that manages a conversation-scoped extended
 * persistence context that can be shared by arbitrary other
 * components.
 * 
 * @author Gavin King
 */
@Scope(ScopeType.CONVERSATION)
@Intercept(NEVER)
public class ManagedPersistenceContext 
   implements Serializable, HttpSessionActivationListener, Mutable, PersistenceContextManager
{

   private static final Log log = LogFactory.getLog(ManagedPersistenceContext.class);
   
   private EntityManager entityManager;
   private String persistenceUnitJndiName;
   private String componentName;
   private ValueBinding<EntityManagerFactory> entityManagerFactory;
   private List<Filter> filters = new ArrayList<Filter>(0);
  
   public boolean clearDirty()
   {
      return true;
   }

   @Create
   public void create(Component component)
   {
      this.componentName = component.getName();
      if (persistenceUnitJndiName==null)
      {
         persistenceUnitJndiName = "java:/" + componentName;
      }
      
      createEntityManager();
      
      PersistenceContexts.instance().touch(componentName);
      
      if ( log.isDebugEnabled() )
      {
         if (entityManagerFactory==null)
         {
            log.debug("created seam managed persistence context for persistence unit: "+ persistenceUnitJndiName);
         }
         else 
         {
            log.debug("created seam managed persistence context from EntityManagerFactory");
         }
      }
   }

   private void createEntityManager()
   {
      entityManager = getEntityManagerFactoryFromJndiOrValueBinding().createEntityManager();      
      setFlushMode( PersistenceContexts.instance().getFlushMode() );
      for (Filter f: filters)
      {
         PersistenceProvider.instance().enableFilter(f, entityManager);
      }
   }

   @Unwrap
   public EntityManager getEntityManager() throws NamingException, SystemException
   {
      if ( !Lifecycle.isDestroying() && Transactions.isTransactionActive() )
      {
         entityManager.joinTransaction();
      }
      return entityManager;
   }
   
   //we can't use @PrePassivate because it is intercept NEVER
   public void sessionWillPassivate(HttpSessionEvent event)
   {
      if ( !PersistenceProvider.instance().isDirty(entityManager) )
      {
         entityManager.close();
         entityManager = null;
      }
   }
   
   //we can't use @PostActivate because it is intercept NEVER
   public void sessionDidActivate(HttpSessionEvent event)
   {
      if (entityManager==null)
      {
         createEntityManager();
      }
   }
   
   @Destroy
   public void destroy()
   {
      if ( log.isDebugEnabled() )
      {
         log.debug("destroying seam managed persistence context for persistence unit: " + persistenceUnitJndiName);
      }
      entityManager.close();
   }
   
   public EntityManagerFactory getEntityManagerFactoryFromJndiOrValueBinding()
   {
      if (entityManagerFactory==null)
      {
         try
         {
            return (EntityManagerFactory) Naming.getInitialContext().lookup(persistenceUnitJndiName);
         }
         catch (NamingException ne)
         {
            throw new IllegalArgumentException("EntityManagerFactory not found in JNDI", ne);
         }
      }
      else
      {
         return entityManagerFactory.getValue();
      }
   }
   
   /**
    * A value binding expression that returns an EntityManagerFactory,
    * for use of JPA outside of Java EE 5 / Embeddable EJB3.
    */
   public ValueBinding<EntityManagerFactory> getEntityManagerFactory()
   {
      return entityManagerFactory;
   }

   public void setEntityManagerFactory(ValueBinding<EntityManagerFactory> entityManagerFactory)
   {
      this.entityManagerFactory = entityManagerFactory;
   }
   
   /**
    * The JNDI name of the EntityManagerFactory, for 
    * use of JPA in Java EE 5 / Embeddable EJB3.
    */
   public String getPersistenceUnitJndiName()
   {
      return persistenceUnitJndiName;
   }

   public void setPersistenceUnitJndiName(String persistenceUnitName)
   {
      this.persistenceUnitJndiName = persistenceUnitName;
   }

   public String getComponentName() {
      return componentName;
   }

   /**
    * Hibernate filters to enable automatically
    */
   protected List<Filter> getFilters()
   {
      return filters;
   }

   protected void setFilters(List<Filter> filters)
   {
      this.filters = filters;
   }

   public void setFlushMode(org.jboss.seam.annotations.FlushModeType flushMode)
   {
      switch (flushMode)
      {
         case AUTO:
            entityManager.setFlushMode(javax.persistence.FlushModeType.AUTO);
            break;
         case COMMIT:
            entityManager.setFlushMode(javax.persistence.FlushModeType.COMMIT);
            break;
         case MANUAL:
            PersistenceProvider.instance().setFlushModeManual(entityManager);
            break;
      }
   }
   
   public String toString()
   {
      return "ManagedPersistenceContext(" + persistenceUnitJndiName + ")";
   }

}

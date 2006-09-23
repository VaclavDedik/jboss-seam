//$Id$
package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.io.Serializable;

import javax.ejb.PostActivate;
import javax.ejb.PrePassivate;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.transaction.SystemException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Mutable;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.util.Naming;
import org.jboss.seam.util.Persistence;
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
@Mutable
public class ManagedPersistenceContext implements Serializable
{

   private static final Log log = LogFactory.getLog(ManagedPersistenceContext.class);
   
   private EntityManager entityManager;
   private String persistenceUnitJndiName;
   private String componentName;
   
   @Create
   public void create(Component component)
   {
      this.componentName = component.getName();
      if (persistenceUnitJndiName==null)
      {
         persistenceUnitJndiName = "java:/" + componentName;
      }
      
      createEntityManager();
      
      TouchedContexts.instance().add(componentName);
      
      if ( log.isDebugEnabled() )
      {
         log.debug("created seam managed persistence context for persistence unit: "+ persistenceUnitJndiName);
      }
   }

   private void createEntityManager()
   {
      try
      {
         entityManager = getEntityManagerFactory().createEntityManager();
      }
      catch (NamingException ne)
      {
         throw new IllegalArgumentException("EntityManagerFactory not found", ne);
      }
      
      switch ( Conversation.instance().getFlushMode() )
      {
         case AUTO: 
            break;
         case MANUAL:
            Persistence.setFlushModeManual(entityManager); 
            break;
         case COMMIT: 
            entityManager.setFlushMode(FlushModeType.COMMIT); 
            break;
      }
   }

   @Unwrap
   public EntityManager getEntityManager() throws NamingException, SystemException
   {
      if ( Transactions.isTransactionActive() ) 
      {
         entityManager.joinTransaction();
      }
      return entityManager;
   }
   
   @PrePassivate
   public void passivate()
   {
      if ( !Conversation.instance().getFlushMode().dirtyBetweenTransactions() ) //unfortunately, we have no isDirty() method!
      {
         entityManager.close();
         entityManager = null;
      }
   }
   
   @PostActivate
   public void activate()
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
   
   private EntityManagerFactory getEntityManagerFactory()
         throws NamingException
   {
      return (EntityManagerFactory) Naming.getInitialContext().lookup(persistenceUnitJndiName);
   }
   
   /**
    * The JNDI name of the EntityManagerFactory
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

   public String toString()
   {
      return "ManagedPersistenceContext(" + persistenceUnitJndiName + ")";
   }
   
}

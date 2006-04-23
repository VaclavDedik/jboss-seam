//$Id$
package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.io.Serializable;

import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
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
public class ManagedPersistenceContext implements Serializable
{
   //TODO: a conversation-scope mutable component, could break in a cluster

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
      
      try
      {
         entityManager = getEntityManagerFactory().createEntityManager();
      }
      catch (NamingException ne)
      {
         throw new IllegalArgumentException("EntityManagerFactory not found", ne);
      }
      
      log.debug("created seam managed persistence context for persistence unit: "+ persistenceUnitJndiName);
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
   
   @Destroy
   public void destroy()
   {
      log.debug("destroying seam managed persistence context for persistence unit: " + persistenceUnitJndiName);
      entityManager.close();
   }
   
   private EntityManagerFactory getEntityManagerFactory()
         throws NamingException
   {
      return (EntityManagerFactory) Naming.getInitialContext().lookup(persistenceUnitJndiName);
   }
   
   public String toString()
   {
      return "ManagedPersistenceContext(" + persistenceUnitJndiName + ")";
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
}

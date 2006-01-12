//$Id$
package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.io.Serializable;

import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContextType;

import org.jboss.logging.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.util.NamingHelper;

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
   private static final Logger log = Logger.getLogger(ManagedPersistenceContext.class);
   
   private EntityManager entityManager;
   private String persistenceUnitName;
   private String jndiName;
   
   @Create
   public void create(Component component)
   {
      if (persistenceUnitName==null)
      {
         persistenceUnitName = component.getName();
      }
      
      try
      {
         entityManager = getEntityManagerFactory(persistenceUnitName)
               .createEntityManager(PersistenceContextType.EXTENDED);
      }
      catch (NamingException ne)
      {
         throw new IllegalArgumentException("EntityManagerFactory not found", ne);
      }
      
      log.debug("created seam managed persistence context for persistence unit: "+ persistenceUnitName);
   }
   
   @Unwrap
   public EntityManager getEntityManager()
   {
      entityManager.isOpen();
      return entityManager;
   }
   
   @Destroy
   public void destroy()
   {
      log.debug("destroying seam managed persistence context for persistence unit: " + persistenceUnitName);
      entityManager.close();
   }
   
   private EntityManagerFactory getEntityManagerFactory(String persistenceUnit)
         throws NamingException
   {
      if (jndiName==null)
      {
          return (EntityManagerFactory) NamingHelper.getInitialContext()
                .lookup("java:/EntityManagerFactories/" + persistenceUnit);
      }
      else
      {
          return (EntityManagerFactory) NamingHelper.getInitialContext().lookup(jndiName);
      }
   }
   
   public String toString()
   {
      return "ManagedPersistenceContext(" + persistenceUnitName + ")";
   }

   public String getPersistenceUnitName()
   {
      return persistenceUnitName;
   }

   public void setPersistenceUnitName(String persistenceUnitName)
   {
      this.persistenceUnitName = persistenceUnitName;
   }

   public String getJndiName() 
   {
       return jndiName;
   }
    
   public void setJndiName(String jndiName) 
   {
       this.jndiName = jndiName;
   }
}

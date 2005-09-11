//$Id$
package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.io.Serializable;

import javax.naming.InitialContext;
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
      
      log.info("created seam managed persistence context for persistence unit: "+ persistenceUnitName);
   }
   
   @Unwrap
   public EntityManager getEntityManager()
   {
      return entityManager;
   }
   
   @Destroy
   public void destroy()
   {
      log.info("destroying seam managed persistence context for persistence unit: " + persistenceUnitName);
      entityManager.close();
   }
   
   private EntityManagerFactory getEntityManagerFactory(String persistenceUnit)
         throws NamingException
   {
      return (EntityManagerFactory) new InitialContext()
            .lookup("java:/EntityManagerFactories/" + persistenceUnit);
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
}

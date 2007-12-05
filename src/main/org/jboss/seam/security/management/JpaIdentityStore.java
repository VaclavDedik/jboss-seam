package org.jboss.seam.security.management;

import static org.jboss.seam.ScopeType.APPLICATION;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

/**
 * The default identity store implementation, uses JPA as its persistence mechanism.
 * 
 * @author Shane Bryzak
 */
@Scope(APPLICATION)
@BypassInterceptors
public class JpaIdentityStore extends IdentityStore
{  
   private Class<? extends UserAccount> accountClass;
   
   private String entityManagerName = "entityManager";
   
   @Override
   protected UserAccount createAccount(String username, String password)
   {
      try
      {
         if (accountClass == null)
         {
            throw new CreateAccountException("Could not create account, accountClass not set");
         }
         
         UserAccount account = accountClass.newInstance(); 
         account.setUsername(username);
         
         if (password == null)
         {
            account.setEnabled(false);
         }
         else
         {
            hashAccountPassword(account, password);
            account.setEnabled(true);            
         }
         
         persistAccount(account);
         
         return account;
      }
      catch (Exception ex)
      {
         if (ex instanceof CreateAccountException)
         {
            throw (CreateAccountException) ex;
         }
         else
         {
            throw new CreateAccountException("Could not create account", ex);
         }
      }
   }   
   
   protected void persistAccount(UserAccount account)
   {
      getEntityManager().persist(account);
   }
   
   private EntityManager getEntityManager()
   {
      return (EntityManager) Component.getInstance(entityManagerName);
   }
   
   public String getEntityManagerName()
   {
      return entityManagerName;
   }
   
   public void setEntityManagerName(String name)
   {
      this.entityManagerName = name;
   }
   
   public Class<? extends UserAccount> getAccountClass()
   {
      return accountClass;
   }
   
   public void setAccountClass(Class<? extends UserAccount> accountClass)
   {
      this.accountClass = accountClass;
   }   
}

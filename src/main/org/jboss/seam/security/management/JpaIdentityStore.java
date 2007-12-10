package org.jboss.seam.security.management;

import static org.jboss.seam.ScopeType.APPLICATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Create;
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
   
   private Set<UserAccount> roleCache;
   
   @Create
   public void init()
   {
      loadRoles();
   }
   
   protected void loadRoles()
   {
      List<? extends UserAccount> roles = getEntityManager().createQuery(
            "from " + accountClass.getName() + " where enabled = true and accountType = :accountType")
            .setParameter("accountType", UserAccount.AccountType.role)
            .getResultList();
      
      roleCache = new HashSet<UserAccount>();
      roleCache.addAll(roles);      
   }
   
   @Override
   protected UserAccount createAccount(String username, String password)
   {
      try
      {
         if (accountClass == null)
         {
            throw new IdentityManagementException("Could not create account, accountClass not set");
         }
         
         UserAccount account = accountClass.newInstance();
         account.setAccountType(UserAccount.AccountType.user);
         account.setUsername(username);
         
         if (password == null)
         {
            account.setEnabled(false);
         }
         else
         {
            account.setPasswordHash(hashPassword(password));
            account.setEnabled(true);            
         }
         
         persistAccount(account);
         
         return account;
      }
      catch (Exception ex)
      {
         if (ex instanceof IdentityManagementException)
         {
            throw (IdentityManagementException) ex;
         }
         else
         {
            throw new IdentityManagementException("Could not create account", ex);
         }
      }
   }
   
   @Override
   public boolean grantRole(String name, String role)
   {
      UserAccount account = getAccount(name);
      
      if (account == null)
      {
         throw new IdentityManagementException("No such account: " + name);
      }
      
      UserAccount roleToGrant = getRole(role);
      
      if (roleToGrant == null)
      {
         throw new IdentityManagementException("No such role: " + role);
      }
      
      if (account.getMemberships() == null)
      {
         account.setMemberships(new HashSet<UserAccount>());
      }
      else if (account.getMemberships().contains(roleToGrant))
      {
         return false;
      }

      account.getMemberships().add(roleToGrant);
      
      return true;
   }
   
   @Override
   public boolean revokeRole(String name, String role)
   {
      UserAccount account = getAccount(name);
      
      if (account == null)
      {
         throw new IdentityManagementException("No such account: " + name);
      }
      
      UserAccount roleToRevoke = getRole(role);
      
      if (roleToRevoke == null)
      {
         throw new IdentityManagementException("No such role: " + role);
      }
      
      return account.getMemberships().remove(roleToRevoke);
   }
   
   @Override
   public List<String> getGrantedRoles(String name)
   {
      UserAccount account = getAccount(name);
      
      if (account == null)
      {
         return null;
      }
      else
      {
         List<String> roles = new ArrayList<String>();
         
         for (UserAccount membership : account.getMemberships())
         {
            if (membership.getAccountType().equals(UserAccount.AccountType.role))
            {
               roles.add(membership.getUsername());
            }
         }
         
         return roles;
      }      
   }
   
   protected UserAccount getAccount(String name)
   {
      return (UserAccount) getEntityManager().createQuery(
            "from " + accountClass.getName() + " where username = :username")
            .setParameter("username", name)
            .getSingleResult();      
   }
   
   protected UserAccount getRole(String name)
   {
      for (UserAccount ua : roleCache)
      {
         if (ua.getUsername().equals(name))
         {
            return ua;
         }
      }
      
      UserAccount ua = getAccount(name); 
      
      if (ua.getAccountType().equals(UserAccount.AccountType.role))
      {
         return ua;
      }
      else
      {
         throw new RuntimeException("No such role: " + name);
      }
   }
   
   @Override
   public List<String> listUsers()
   {
      return getEntityManager().createQuery(
            "select username from " + accountClass.getName() + 
            " where accountType = :accountType")
            .setParameter("accountType", UserAccount.AccountType.user)
            .getResultList();      
   }
   
   @Override
   public List<String> listUsers(String filter)
   {
      return getEntityManager().createQuery(
            "select username from " + accountClass.getName() + 
            " where accountType = :accountType and lower(username) like :username")
            .setParameter("accountType", UserAccount.AccountType.user)
            .setParameter("username", "%" + (filter != null ? filter.toLowerCase() : "") + 
                  "%")
            .getResultList();
   }

   @Override
   public List<String> listRoles()
   {
      return getEntityManager().createQuery(
            "select username from " + accountClass.getName() + 
            " where accountType = :accountType")
            .setParameter("accountType", UserAccount.AccountType.role)
            .getResultList();      
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

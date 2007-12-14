package org.jboss.seam.security.management;

import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.security.management.UserAccount.AccountType;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.core.Events;
import org.jboss.seam.util.Hex;

/**
 * The default identity store implementation, uses JPA as its persistence mechanism.
 * 
 * @author Shane Bryzak
 */
@Scope(APPLICATION)
@BypassInterceptors
public class JpaIdentityStore extends IdentityStore
{  
   public static final String EVENT_ACCOUNT_CREATED = "org.jboss.seam.security.management.accountCreated"; 
   public static final String EVENT_ACCOUNT_AUTHENTICATED = "org.jboss.seam.security.management.accountAuthenticated";
   
   private String hashFunction = "MD5";
   private String hashCharset = "UTF-8";   
   
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
   public boolean createAccount(String username, String password)
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
         
         if (Events.exists()) Events.instance().raiseEvent(EVENT_ACCOUNT_CREATED, account);
         
         return true;
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
   public boolean deleteAccount(String name)
   {
      UserAccount account = validateUser(name);
      getEntityManager().remove(account);
      return true;
   }
   
   @Override
   public boolean grantRole(String name, String role)
   {
      UserAccount account = validateUser(name);      
      UserAccount roleToGrant = validateRole(role);
      
      if (account.getMemberships() == null)
      {
         account.setMemberships(new HashSet<UserAccount>());
      }
      else if (account.getMemberships().contains(roleToGrant))
      {
         return false;
      }

      account.getMemberships().add(roleToGrant);
      mergeAccount(account);
      
      return true;
   }
   
   @Override
   public boolean revokeRole(String name, String role)
   {
      UserAccount account = validateUser(name);      
      UserAccount roleToRevoke = validateRole(role);      
      boolean success = account.getMemberships().remove(roleToRevoke);
      mergeAccount(account);
      return success;
   }
   
   @Override
   public boolean enableAccount(String name)
   {
      UserAccount account = validateUser(name);        
      
      // If it's already enabled return false
      if (account.isEnabled())
      {
         return false;
      }
      
      account.setEnabled(true);
      mergeAccount(account);
      
      return true;
   }
   
   @Override
   public boolean disableAccount(String name)
   {
      UserAccount account = validateUser(name);       
      
      // If it's already enabled return false
      if (!account.isEnabled())
      {
         return false;
      }    
      
      account.setEnabled(false);
      mergeAccount(account);
      
      return true;
   }
   
   @Override
   public List<String> getGrantedRoles(String name)
   {
      UserAccount account = validateUser(name);

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
   
   @Override
   public List<String> getImpliedRoles(String name)
   {
      UserAccount account = validateUser(name);

      Set<String> roles = new HashSet<String>();

      for (UserAccount membership : account.getMemberships())
      {
         if (membership.getAccountType().equals(UserAccount.AccountType.role))
         {
            addRoleAndMemberships(membership.getUsername(), roles);
         }
      }            
      
      return new ArrayList<String>(roles);
   }
   
   private void addRoleAndMemberships(String role, Set<String> roles)
   {
      UserAccount roleAccount = validateRole(role);
      roles.add(role);
      
      for (UserAccount membership : roleAccount.getMemberships())
      {
         if (!roles.contains(membership.getUsername()))
         {
            addRoleAndMemberships(membership.getUsername(), roles);
         }
      }            
   }
   
   @Override
   public boolean authenticate(String username, String password)
   {
      UserAccount account = validateUser(username);
      
      if (account == null || !account.getAccountType().equals(AccountType.user)
            || !account.isEnabled())
      {
         return false;
      }
      
      boolean success = hashPassword(password).equals(account.getPasswordHash());
      
      if (success && Events.exists())
      {
         Events.instance().raiseEvent(EVENT_ACCOUNT_AUTHENTICATED, account);
      }
      
      return success;
   }
   
   /**
    * Retrieves a user UserAccount from persistent storage.  If the UserAccount does
    * not exist, an IdentityManagementException is thrown.
    * 
    * @param name The user's username
    * @return The UserAccount for the specified user
    */
   protected UserAccount validateUser(String name)
   {      
      try
      {
         return (UserAccount) getEntityManager().createQuery(
            "from " + accountClass.getName() + " where username = :username and " +
            "accountType = :accountType")
            .setParameter("username", name)
            .setParameter("accountType", AccountType.user)
            .getSingleResult();
      }
      catch (NoResultException ex)
      {
         throw new IdentityManagementException("No such user: " + name);         
      }
   }
   
   /**
    * Retrieves a role UserAccount from persistent storage.  If the UserAccount
    * does not exist, an IdentityManagementException is thrown.
    * 
    * @param name The role name
    * @return The UserAccount for the specific role
    */
   protected UserAccount validateRole(String name)
   {
      // The role *should* be cached
      for (UserAccount ua : roleCache)
      {
         if (ua.getUsername().equals(name))
         {
            return ua;
         }
      }
      
      try
      {
         // As a last ditch effort, check the db
         UserAccount role = (UserAccount) getEntityManager().createQuery(
            "from " + accountClass.getName() + " where username = :username and " +
            "accountType = :accountType")
            .setParameter("username", name)
            .setParameter("accountType", AccountType.role)
            .getSingleResult();
         
         roleCache.add(role);
         
         return role;
      }
      catch (NoResultException ex)
      {
         throw new IdentityManagementException("No such role: " + name);         
      }      
   }
   
   @Override
   public List<String> listUsers()
   {
      return getEntityManager().createQuery(
            "select username from " + accountClass.getName() + 
            " where accountType = :accountType")
            .setParameter("accountType", AccountType.user)
            .getResultList();      
   }
   
   @Override
   public List<String> listUsers(String filter)
   {
      return getEntityManager().createQuery(
            "select username from " + accountClass.getName() + 
            " where accountType = :accountType and lower(username) like :username")
            .setParameter("accountType", AccountType.user)
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
            .setParameter("accountType", AccountType.role)
            .getResultList();      
   }   
   
   protected void persistAccount(UserAccount account)
   {
      getEntityManager().persist(account);
   }
   
   protected UserAccount mergeAccount(UserAccount account)
   {
      return getEntityManager().merge(account);
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
   
   
   protected String hashPassword(String password)
   {
      try {
         MessageDigest md = MessageDigest.getInstance(hashFunction);
         md.update(password.getBytes(hashCharset));         
         byte[] raw = md.digest();
         
         // TODO - salt the hash, possibly using the user name? 
         return new String(Hex.encodeHex(raw));
     } 
     catch (Exception e) {
         throw new RuntimeException(e);        
     }      
   }   
}

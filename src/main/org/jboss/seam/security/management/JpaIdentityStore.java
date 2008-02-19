package org.jboss.seam.security.management;

import static org.jboss.seam.ScopeType.APPLICATION;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Events;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.management.UserAccount.AccountType;
import org.jboss.seam.util.Hex;

/**
 * The default identity store implementation, uses JPA as its persistence mechanism.
 * 
 * @author Shane Bryzak
 */
@Scope(APPLICATION)
@BypassInterceptors
public class JpaIdentityStore implements IdentityStore
{  
   public static final String AUTHENTICATED_USER = "org.jboss.seam.security.management.authenticatedUser";
   
   public static final String EVENT_ACCOUNT_CREATED = "org.jboss.seam.security.management.accountCreated"; 
   public static final String EVENT_ACCOUNT_AUTHENTICATED = "org.jboss.seam.security.management.accountAuthenticated";
   
   private String hashFunction = "MD5";
   private String hashCharset = "UTF-8";
   
   private String entityManagerName = "entityManager";
   
   private Class<? extends UserAccount> accountClass;
   
   private Map<String,Set<String>> roleCache;
   
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
      
      roleCache = new HashMap<String,Set<String>>();
      
      for (UserAccount role : roles)
      {
         Set<String> memberships = new HashSet<String>();
         for (UserAccount m : role.getMemberships())
         {
            memberships.add(m.getUsername());
         }
         roleCache.put(role.getUsername(), memberships);
      }      
   }
   
   public boolean createUser(String username, String password)
   {
      try
      {
         if (accountClass == null)
         {
            throw new IdentityManagementException("Could not create account, accountClass not set");
         }
         
         if (userExists(username))
         {
            throw new IdentityManagementException("Could not create account, already exists");
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
            account.setPasswordHash(hashPassword(password, username));
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
   
   public boolean deleteUser(String name)
   {
      UserAccount account = validateAccount(name);
      if (account == null || !account.getAccountType().equals(AccountType.user)) 
      {
         throw new NoSuchUserException("Could not delete account, no such user '" + name + "'");
      }
      
      getEntityManager().remove(account);
      return true;
   }
   
   public boolean grantRole(String name, String role)
   {
      UserAccount account = validateAccount(name);
      if (account == null)
      {
         throw new NoSuchUserException("Could not grant role, no such user or role '" + name + "'");
      }
      
      UserAccount roleToGrant = validateAccount(role);
      if (roleToGrant == null)
      {
         throw new NoSuchRoleException("Could not grant role, role '" + role + "' does not exist");
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
      mergeAccount(account);
      
      return true;
   }
   
   public boolean revokeRole(String name, String role)
   {
      UserAccount account = validateAccount(name);
      if (account == null)
      {
         throw new NoSuchUserException("Could not revoke role, no such user or role '" + name + "'");
      }
      
      UserAccount roleToRevoke = validateAccount(role);
      if (roleToRevoke == null)
      {
         throw new NoSuchRoleException("Could not revoke role, role '" + role + "' does not exist");
      }      
       
      boolean success = account.getMemberships().remove(roleToRevoke);
      mergeAccount(account);
      return success;
   }
   
   public boolean createRole(String role)
   {
      try
      {
         if (accountClass == null)
         {
            throw new IdentityManagementException("Could not create role, accountClass not set");
         }
         
         if (roleExists(role))
         {
            throw new IdentityManagementException("Could not create role, already exists");
         }
         
         UserAccount account = accountClass.newInstance();
         account.setAccountType(UserAccount.AccountType.role);
         account.setUsername(role);
         
         persistAccount(account);
         
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
            throw new IdentityManagementException("Could not create role", ex);
         }
      }      
   }
   
   public boolean deleteRole(String role)
   {      
      UserAccount roleToDelete = validateAccount(role);
      if (roleToDelete == null)
      {
         throw new NoSuchRoleException("Could not delete role, role '" + role + "' does not exist");
      }        
      
      getEntityManager().remove(roleToDelete);
      return true;
   }
   
   public boolean enableUser(String name)
   {
      UserAccount account = validateAccount(name);
      if (account == null || !account.getAccountType().equals(AccountType.user))
      {
         throw new NoSuchUserException("Could not enable account, user '" + name + "' does not exist");
      }
      
      // If it's already enabled return false
      if (account.isEnabled())
      {
         return false;
      }
      
      account.setEnabled(true);
      mergeAccount(account);
      
      return true;
   }
   
   public boolean disableUser(String name)
   {
      UserAccount account = validateAccount(name);
      if (account == null || !account.getAccountType().equals(AccountType.user))
      {
         throw new NoSuchUserException("Could not disable account, user '" + name + "' does not exist");
      }
      
      // If it's already enabled return false
      if (!account.isEnabled())
      {
         return false;
      }    
      
      account.setEnabled(false);
      mergeAccount(account);
      
      return true;
   }
   
   public boolean changePassword(String name, String password)
   {
      UserAccount account = validateAccount(name);
      if (account == null || !account.getAccountType().equals(AccountType.user))
      {
         throw new NoSuchUserException("Could not change password, user '" + name + "' does not exist");
      }
      
      account.setPasswordHash(hashPassword(password, name));
      mergeAccount(account);
      return true;
   }
   
   public boolean userExists(String name)
   {
      UserAccount account = validateAccount(name);
      return account != null && account.getAccountType().equals(AccountType.user);
   }
   
   public boolean roleExists(String name)
   {
      UserAccount role = validateAccount(name);
      return role != null && role.getAccountType().equals(AccountType.role);
   }
   
   public boolean isUserEnabled(String name)
   {
      UserAccount account = validateAccount(name);
      return account != null && account.getAccountType().equals(AccountType.user)
             && account.isEnabled();
   }
   
   public List<String> getGrantedRoles(String name)
   {
      UserAccount account = validateAccount(name);
      if (account == null) throw new NoSuchUserException("No such user '" + name + "'");      

      List<String> roles = new ArrayList<String>();      
      if (account.getMemberships() != null)
      {
         for (UserAccount membership : account.getMemberships())
         {
            if (membership.getAccountType().equals(UserAccount.AccountType.role))
            {
               roles.add(membership.getUsername());
            }
         }
      }
      
      return roles;     
   }
   
   public List<String> getImpliedRoles(String name)
   {
      UserAccount account = validateAccount(name);
      if (account == null) throw new NoSuchUserException("No such user '" + name + "'"); 

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
      roles.add(role);
      
      for (String membership : roleCache.get(role))
      {
         if (!roles.contains(membership))
         {
            addRoleAndMemberships(membership, roles);
         }
      }            
   }
   
   public boolean authenticate(String username, String password)
   {
      UserAccount account = validateAccount(username);          
      if (account == null || !account.getAccountType().equals(AccountType.user)
            || !account.isEnabled())
      {
         return false;
      }
      
      boolean success = hashPassword(password, username).equals(account.getPasswordHash());
            
      if (success && Events.exists())
      {
         if (Contexts.isEventContextActive())
         {
            Contexts.getEventContext().set(AUTHENTICATED_USER, account);
         }
         
         Events.instance().raiseEvent(EVENT_ACCOUNT_AUTHENTICATED, account);
      }
      
      return success;
   }
   
   @Observer(Identity.EVENT_POST_AUTHENTICATE)
   public void setUserAccountForSession()
   {
      if (Contexts.isEventContextActive() && Contexts.isSessionContextActive())
      {
         Contexts.getSessionContext().set(AUTHENTICATED_USER, 
               Contexts.getEventContext().get(AUTHENTICATED_USER));
      }
   }
   
   protected UserAccount validateAccount(String name)       
   {
      try
      {
         UserAccount account = (UserAccount) getEntityManager().createQuery(
            "from " + accountClass.getName() + " where username = :username")
            .setParameter("username", name)
            .getSingleResult();
         
         if (account.getAccountType().equals(AccountType.role) && 
             !roleCache.containsKey(account.getUsername()))
         {
            Set<String> memberships = new HashSet<String>();
            for (UserAccount m : account.getMemberships())
            {
               memberships.add(m.getUsername());
            }
            
            roleCache.put(account.getUsername(), memberships);  
         }
         
         return account;
      }
      catch (NoResultException ex)
      {
         return null;        
      }      
   }
   
   public List<String> listUsers()
   {
      return getEntityManager().createQuery(
            "select username from " + accountClass.getName() + 
            " where accountType = :accountType")
            .setParameter("accountType", AccountType.user)
            .getResultList();      
   }
   
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
   
   public Class<? extends UserAccount> getAccountClass()
   {
      return accountClass;
   }
   
   public void setAccountClass(Class<? extends UserAccount> accountClass)
   {
      this.accountClass = accountClass;
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
   
   protected String hashPassword(String password, String saltPhrase)
   {
      try {
         MessageDigest md = MessageDigest.getInstance(hashFunction);
         
         md.update(saltPhrase.getBytes());
         byte[] salt = md.digest();
         
         md.reset();
         md.update(password.getBytes(hashCharset));
         md.update(salt);
         
         byte[] raw = md.digest();
         
         return new String(Hex.encodeHex(raw));
     } 
     catch (Exception e) {
         throw new RuntimeException(e);        
     }      
   }   
}

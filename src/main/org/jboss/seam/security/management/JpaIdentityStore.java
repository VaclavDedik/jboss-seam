package org.jboss.seam.security.management;

import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Events;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.core.Expressions.ValueExpression;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.management.UserAccount.AccountType;

/**
 * The default identity store implementation, uses JPA as its persistence mechanism.
 * 
 * @author Shane Bryzak
 */
@Name("org.jboss.seam.security.management.jpaIdentityStore")
@Install(precedence = BUILT_IN, value=false) 
@Scope(APPLICATION)
@BypassInterceptors
public class JpaIdentityStore implements IdentityStore, Serializable
{  
   public static final String AUTHENTICATED_USER = "org.jboss.seam.security.management.authenticatedUser";
   
   public static final String EVENT_ACCOUNT_CREATED = "org.jboss.seam.security.management.accountCreated"; 
   public static final String EVENT_ACCOUNT_AUTHENTICATED = "org.jboss.seam.security.management.accountAuthenticated";
   
   protected FeatureSet featureSet = new FeatureSet(FeatureSet.FEATURE_ALL);
   
   private ValueExpression<EntityManager> entityManager;  
   
   private Class<? extends UserAccount> accountClass;
   
   private Map<String,Set<String>> roleCache;
   
   private Field firstNameField;
   private Field lastNameField;
   
   private String firstNameFieldName;   
   private String lastNameFieldName;
   
   public String getFirstNameField()
   {
      return firstNameFieldName;
   }
   
   public void setFirstNameField(String firstNameFieldName)
   {
      this.firstNameFieldName = firstNameFieldName;
   }
   
   public String getLastNameField()
   {
      return lastNameFieldName;
   }
   
   public void setLastNameField(String lastNameFieldName)
   {
      this.lastNameFieldName = lastNameFieldName;
   }
   
   public int getFeatures()
   {
      return featureSet.getFeatures();
   }
   
   public void setFeatures(int features)
   {
      featureSet = new FeatureSet(features);
   }
   
   public boolean supportsFeature(int feature)
   {
      return featureSet.supports(feature);
   }
   
   @Create
   public void init()
   {
      if (entityManager == null)
      {
         entityManager = Expressions.instance().createValueExpression("#{entityManager}", EntityManager.class);
      }
      
      loadRoles();
      
      if (getFirstNameField() != null)
      {
         try
         {
            firstNameField = accountClass.getField(getFirstNameField());
         }
         catch (NoSuchFieldException ex)
         {
            throw new RuntimeException("First name field " + getFirstNameField() + " does not exist " +
                  "in account class " + accountClass.getName(), ex);
         }
      }
      
      if (getLastNameField() != null)
      {
         try
         {
            lastNameField = accountClass.getField(getLastNameField());
         }
         catch (NoSuchFieldException ex)
         {
            throw new RuntimeException("Last name field " + getLastNameField() + " does not exist " +
                  "in account class " + accountClass.getName(), ex);
         }
      }      
   }
   
   protected void loadRoles()
   {
      List<? extends UserAccount> roles = lookupEntityManager().createQuery(
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
   
   private void setFieldValue(Field field, Object instance, Object value) throws Exception
   {
      boolean accessible = field.isAccessible();
      try
      {
         field.setAccessible(true);
         field.set(instance, value);
      }
      finally
      {
         field.setAccessible(accessible);
      }
   }
   
   public boolean createUser(String username, String password, String firstname, String lastname)
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
         
         if (firstNameField != null) setFieldValue(firstNameField, account, firstname);         
         if (lastNameField != null) setFieldValue(lastNameField, account, lastname);
         
         if (password == null)
         {
            account.setEnabled(false);
         }
         else
         {
            account.setPasswordHash(PasswordHash.generateHash(password, getAccountSalt(account)));
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
   
   protected String getAccountSalt(UserAccount account)
   {
      return account.getUsername();
   }
   
   public boolean createUser(String username, String password)
   {
      return createUser(username, password, null, null);
   }
   
   public boolean deleteUser(String name)
   {
      UserAccount account = validateAccount(name);
      if (account == null || !account.getAccountType().equals(AccountType.user)) 
      {
         throw new NoSuchUserException("Could not delete account, no such user '" + name + "'");
      }
      
      lookupEntityManager().remove(account);
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
      
      lookupEntityManager().remove(roleToDelete);
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
      
      account.setPasswordHash(PasswordHash.generateHash(password, getAccountSalt(account)));
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
      
      String passwordHash = PasswordHash.generateHash(password, getAccountSalt(account));
      boolean success = passwordHash.equals(account.getPasswordHash());
            
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
         UserAccount account = (UserAccount) lookupEntityManager().createQuery(
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
      return lookupEntityManager().createQuery(
            "select username from " + accountClass.getName() + 
            " where accountType = :accountType")
            .setParameter("accountType", AccountType.user)
            .getResultList();      
   }
   
   public List<String> listUsers(String filter)
   {
      return lookupEntityManager().createQuery(
            "select username from " + accountClass.getName() + 
            " where accountType = :accountType and lower(username) like :username")
            .setParameter("accountType", AccountType.user)
            .setParameter("username", "%" + (filter != null ? filter.toLowerCase() : "") + 
                  "%")
            .getResultList();
   }

   public List<String> listRoles()
   {
      return lookupEntityManager().createQuery(
            "select username from " + accountClass.getName() + 
            " where accountType = :accountType")
            .setParameter("accountType", AccountType.role)
            .getResultList();      
   }   
   
   protected void persistAccount(UserAccount account)
   {
      lookupEntityManager().persist(account);
   }
   
   protected UserAccount mergeAccount(UserAccount account)
   {
      return lookupEntityManager().merge(account);
   }
   
   public Class<? extends UserAccount> getAccountClass()
   {
      return accountClass;
   }
   
   public void setAccountClass(Class<? extends UserAccount> accountClass)
   {
      this.accountClass = accountClass;
   }   
   
   private EntityManager lookupEntityManager()
   {
      return entityManager.getValue();
   }
   
   public ValueExpression getEntityManager()
   {
      return entityManager;
   }
   
   public void setEntityManager(ValueExpression expression)
   {
      this.entityManager = expression;
   }         
}

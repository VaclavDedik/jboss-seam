package org.jboss.seam.security.management;

import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.annotations.security.management.RoleConditional;
import org.jboss.seam.annotations.security.management.RoleGroups;
import org.jboss.seam.annotations.security.management.RoleName;
import org.jboss.seam.annotations.security.management.UserEnabled;
import org.jboss.seam.annotations.security.management.UserFirstName;
import org.jboss.seam.annotations.security.management.UserLastName;
import org.jboss.seam.annotations.security.management.UserPassword;
import org.jboss.seam.annotations.security.management.UserPrincipal;
import org.jboss.seam.annotations.security.management.UserRoles;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Events;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.core.Expressions.ValueExpression;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.security.Identity;
import org.jboss.seam.util.AnnotatedBeanProperty;

/**
 * The default identity store implementation, uses JPA as its persistence mechanism.
 * 
 * @author Shane Bryzak
 */
@Name("org.jboss.seam.security.jpaIdentityStore")
@Install(precedence = BUILT_IN, value=false) 
@Scope(APPLICATION)
@BypassInterceptors
public class JpaIdentityStore implements IdentityStore, Serializable
{  
   public static final String AUTHENTICATED_USER = "org.jboss.seam.security.management.authenticatedUser";
   
   public static final String EVENT_USER_CREATED = "org.jboss.seam.security.management.userCreated";
   public static final String EVENT_PRE_PERSIST_USER = "org.jboss.seam.security.management.prePersistUser";
   public static final String EVENT_USER_AUTHENTICATED = "org.jboss.seam.security.management.userAuthenticated";
   
   private static final LogProvider log = Logging.getLogProvider(JpaIdentityStore.class);    
   
   protected FeatureSet featureSet;
   
   private ValueExpression<EntityManager> entityManager;  
   
   private Class userClass;
   private Class roleClass;   
   
   private AnnotatedBeanProperty<UserPrincipal> userPrincipalProperty;
   private AnnotatedBeanProperty<UserPassword> userPasswordProperty;
   private AnnotatedBeanProperty<UserRoles> userRolesProperty;
   private AnnotatedBeanProperty<UserEnabled> userEnabledProperty;
   private AnnotatedBeanProperty<UserFirstName> userFirstNameProperty;
   private AnnotatedBeanProperty<UserLastName> userLastNameProperty;   
   private AnnotatedBeanProperty<RoleName> roleNameProperty;
   private AnnotatedBeanProperty<RoleGroups> roleGroupsProperty;
   private AnnotatedBeanProperty<RoleConditional> roleConditionalProperty;
   
   public Set<Feature> getFeatures()
   {
      return featureSet.getFeatures();
   }
   
   public void setFeatures(Set<Feature> features)
   {
      featureSet = new FeatureSet(features);
   }
   
   public boolean supportsFeature(Feature feature)
   {
      return featureSet.supports(feature);
   }
   
   @Create
   public void init()
   {                  
      if (featureSet == null)
      {
         featureSet = new FeatureSet();
         featureSet.enableAll();
      }      
      
      if (entityManager == null)
      {
         entityManager = Expressions.instance().createValueExpression("#{entityManager}", EntityManager.class);
      }      
      
      if (userClass == null)
      {
         log.debug("Error in JpaIdentityStore configuration - userClass must be configured.");
         return;
      }    
      
      initProperties();   
   }
   
   private void initProperties()
   {
      userPrincipalProperty = AnnotatedBeanProperty.scanForProperty(userClass, UserPrincipal.class);
      userPasswordProperty = AnnotatedBeanProperty.scanForProperty(userClass, UserPassword.class);
      userRolesProperty = AnnotatedBeanProperty.scanForProperty(userClass, UserRoles.class);
      userEnabledProperty = AnnotatedBeanProperty.scanForProperty(userClass, UserEnabled.class);
      userFirstNameProperty = AnnotatedBeanProperty.scanForProperty(userClass, UserFirstName.class);
      userLastNameProperty = AnnotatedBeanProperty.scanForProperty(userClass, UserLastName.class);
       
      if (roleClass != null)
      {
         roleNameProperty = AnnotatedBeanProperty.scanForProperty(roleClass, RoleName.class);
         roleGroupsProperty = AnnotatedBeanProperty.scanForProperty(roleClass, RoleGroups.class);
         roleConditionalProperty = AnnotatedBeanProperty.scanForProperty(roleClass, RoleConditional.class);
      }
      
      if (userPrincipalProperty == null) 
      {
         throw new IdentityManagementException("Invalid userClass " + userClass.getName() + 
               " - required annotation @UserPrincipal not found on any Field or Method.");
      }
      
      if (userRolesProperty == null)
      {
         throw new IdentityManagementException("Invalid userClass " + userClass.getName() + 
         " - required annotation @UserRoles not found on any Field or Method.");         
      }
      
      if (roleClass != null && roleNameProperty == null)
      {
         throw new IdentityManagementException("Invalid roleClass " + roleClass.getName() + 
         " - required annotation @RoleName not found on any Field or Method.");         
      }
   }
   
   public boolean createUser(String username, String password, String firstname, String lastname)
   {
      try
      {
         if (userClass == null)
         {
            throw new IdentityManagementException("Could not create account, userClass not set");
         }
         
         if (userExists(username))
         {
            throw new IdentityManagementException("Could not create account, already exists");
         }
         
         Object user = userClass.newInstance();

         userPrincipalProperty.setValue(user, username);

         if (userFirstNameProperty != null) userFirstNameProperty.setValue(user, firstname);         
         if (userLastNameProperty != null) userLastNameProperty.setValue(user, lastname);
         
         if (password == null)
         {
            if (userEnabledProperty != null) userEnabledProperty.setValue(user, false);
         }
         else
         {            
            userPasswordProperty.setValue(user, generatePasswordHash(password, getUserAccountSalt(user)));
            if (userEnabledProperty != null) userEnabledProperty.setValue(user, true);
         }
         
         if (Events.exists()) Events.instance().raiseEvent(EVENT_PRE_PERSIST_USER, user);
         
         persistEntity(user);
         
         if (Events.exists()) Events.instance().raiseEvent(EVENT_USER_CREATED, user);
         
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
   
   protected String getUserAccountSalt(Object user)
   {
      // By default, we'll use the user's username as the password salt
      return userPrincipalProperty.getValue(user).toString();
   }
   
   public boolean createUser(String username, String password)
   {
      return createUser(username, password, null, null);
   }
   
   public boolean deleteUser(String name)
   {
      Object user = lookupUser(name);
      if (user == null) 
      {
         throw new NoSuchUserException("Could not delete, user '" + name + "' does not exist");
      }
      
      removeEntity(user);
      return true;
   }
   
   public boolean grantRole(String username, String role)
   {
      if (roleClass == null) return false;
      
      Object user = lookupUser(username);
      if (user == null)
      {
         if (userPasswordProperty != null)
         {
            // If no userPasswordProperty is set, it means that authentication is being performed
            // by another identity store and this one is just managing roles
            throw new NoSuchUserException("Could not grant role, no such user '" + username + "'");
         }
         else
         {
            // We need to create a new user object
            if (createUser(username, null))
            {
               user = lookupUser(username);
            }
            else
            {
               throw new IdentityManagementException(
                     "Could not grant role - user does not exist and an attempt to create the user failed.");
            }
         }
      }
      
      Object roleToGrant = lookupRole(role);
      if (roleToGrant == null)
      {
         throw new NoSuchRoleException("Could not grant role, role '" + role + "' does not exist");
      }
      
      Collection userRoles = (Collection) userRolesProperty.getValue(user); 
      if (userRoles == null)
      {
         // This should either be a Set, or a List...
         if (Set.class.isAssignableFrom(userRolesProperty.getPropertyClass()))
         {
            userRoles = new HashSet();
         }
         else if (List.class.isAssignableFrom(userRolesProperty.getPropertyClass()))
         {
            userRoles = new ArrayList();
         }
         
         userRolesProperty.setValue(user, userRoles);
      }
      else if (((Collection) userRolesProperty.getValue(user)).contains(roleToGrant))
      {
         return false;
      }

      ((Collection) userRolesProperty.getValue(user)).add(roleToGrant);
      mergeEntity(user);
      
      return true;
   }   
   
   public boolean revokeRole(String username, String role)
   {
      Object user = lookupUser(username);
      if (user == null)
      {
         throw new NoSuchUserException("Could not revoke role, no such user '" + username + "'");
      }
      
      Object roleToRevoke = lookupRole(role);
      if (roleToRevoke == null)
      {
         throw new NoSuchRoleException("Could not revoke role, role '" + role + "' does not exist");
      }      
       
      boolean success = ((Collection) userRolesProperty.getValue(user)).remove(roleToRevoke);
      
      if (success) mergeEntity(user);
      return success;
   }
   
   public boolean addRoleToGroup(String role, String group)
   {
      if (roleGroupsProperty == null) return false;      
      
      Object targetRole = lookupRole(role);
      if (targetRole == null)
      {
         throw new NoSuchUserException("Could not add role to group, no such role '" + role + "'");
      }
      
      Object targetGroup = lookupRole(group);
      if (targetGroup == null)
      {
         throw new NoSuchRoleException("Could not grant role, group '" + group + "' does not exist");
      }
      

      Collection roleGroups = (Collection) roleGroupsProperty.getValue(targetRole); 
      if (roleGroups == null)
      {
         // This should either be a Set, or a List...
         if (Set.class.isAssignableFrom(roleGroupsProperty.getPropertyClass()))
         {
            roleGroups = new HashSet();
         }
         else if (List.class.isAssignableFrom(roleGroupsProperty.getPropertyClass()))
         {
            roleGroups = new ArrayList();
         }
         
         roleGroupsProperty.setValue(targetRole, roleGroups);
      }
      else if (((Collection) roleGroupsProperty.getValue(targetRole)).contains(targetGroup))
      {
         return false;
      }

      ((Collection) roleGroupsProperty.getValue(targetRole)).add(targetGroup);
      mergeEntity(targetRole);
      
      return true;
   }

   public boolean removeRoleFromGroup(String role, String group)
   {
      if (roleGroupsProperty == null) return false;
      
      Object roleToRemove = lookupRole(role);
      if (role == null)
      {
         throw new NoSuchUserException("Could not remove role from group, no such role '" + role + "'");
      }
      
      Object targetGroup = lookupRole(group);
      if (targetGroup == null)
      {
         throw new NoSuchRoleException("Could not remove role from group, no such group '" + group + "'");
      }      
       
      boolean success = ((Collection) roleGroupsProperty.getValue(roleToRemove)).remove(targetGroup);
      
      if (success) mergeEntity(roleToRemove);
      return success;
   }      
   
   public boolean createRole(String role)
   {
      try
      {
         if (roleClass == null)
         {
            throw new IdentityManagementException("Could not create role, roleClass not set");
         }
         
         if (roleExists(role))
         {
            throw new IdentityManagementException("Could not create role, already exists");
         }
         
         Object instance = roleClass.newInstance();         
         roleNameProperty.setValue(instance, role);         
         persistEntity(instance);
         
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
      Object roleToDelete = lookupRole(role);
      if (roleToDelete == null)
      {
         throw new NoSuchRoleException("Could not delete role, role '" + role + "' does not exist");
      }        
      
      removeEntity(roleToDelete);
      return true;
   }
   
   public boolean enableUser(String name)
   {
      if (userEnabledProperty == null)
      {
         log.debug("Can not enable user, no @UserEnabled property configured in userClass " + userClass.getName());
         return false;
      }
      
      Object user = lookupUser(name);
      if (user == null)
      {
         throw new NoSuchUserException("Could not enable user, user '" + name + "' does not exist");
      }
      
      // Can't enable an already-enabled user, return false
      if (((Boolean) userEnabledProperty.getValue(user)) == true)
      {
         return false;
      }
      
      userEnabledProperty.setValue(user, true);
      mergeEntity(user);      
      return true;
   }
   
   public boolean disableUser(String name)
   {
      if (userEnabledProperty == null)
      {
         log.debug("Can not disable user, no @UserEnabled property configured in userClass " + userClass.getName());
         return false;
      }
      
      Object user = lookupUser(name);
      if (user == null)
      {
         throw new NoSuchUserException("Could not disable user, user '" + name + "' does not exist");
      }
      
      // Can't disable an already-disabled user, return false
      if (((Boolean) userEnabledProperty.getValue(user)) == false)
      {
         return false;
      }          
      
      userEnabledProperty.setValue(user, false);
      mergeEntity(user);
      
      return true;
   }
   
   public boolean changePassword(String username, String password)
   {
      Object user = lookupUser(username);
      if (user == null)
      {
         throw new NoSuchUserException("Could not change password, user '" + username + "' does not exist");
      }
      
      userPasswordProperty.setValue(user, generatePasswordHash(password, getUserAccountSalt(user)));
      mergeEntity(user);
      return true;
   }
   
   public boolean userExists(String name)
   {
      return lookupUser(name) != null;
   }
   
   public boolean roleExists(String name)
   {
      return lookupRole(name) != null;
   }
   
   public boolean isUserEnabled(String name)
   {
      Object user = lookupUser(name);
      return user != null && (userEnabledProperty == null || (((Boolean) userEnabledProperty.getValue(user))) == true);
   }
   
   public List<String> getGrantedRoles(String name)
   {
      Object user = lookupUser(name);
      if (user == null)
      {
         throw new NoSuchUserException("No such user '" + name + "'");      
      }

      List<String> roles = new ArrayList<String>();
      Collection userRoles = (Collection) userRolesProperty.getValue(user);
      if (userRoles != null)
      {
         for (Object role : userRoles)
         {
            roles.add((String) roleNameProperty.getValue(role));
         }
      }
      
      return roles;     
   }
   
   public List<String> getRoleGroups(String name)
   {
      Object role = lookupRole(name);
      if (role == null)
      {
         throw new NoSuchUserException("No such role '" + name + "'");
      }

      List<String> groups = new ArrayList<String>();
      
      if (roleGroupsProperty != null)
      {
         Collection roleGroups = (Collection) roleGroupsProperty.getValue(role);
         if (roleGroups != null)
         {
            for (Object group : roleGroups)
            {
               groups.add((String) roleNameProperty.getValue(group));
            }
         }
      }
      
      return groups;      
   }
   
   public List<String> getImpliedRoles(String name)
   {
      Object user = lookupUser(name);
      if (user == null) 
      {
         throw new NoSuchUserException("No such user '" + name + "'"); 
      }

      Set<String> roles = new HashSet<String>();
      Collection userRoles = (Collection) userRolesProperty.getValue(user);
      if (userRoles != null)
      {
         for (Object role : userRoles)
         {
            addRoleAndMemberships((String) roleNameProperty.getValue(role), roles);
         }
      }
      
      return new ArrayList<String>(roles);
   }
   
   private void addRoleAndMemberships(String role, Set<String> roles)
   {
      if (roles.add(role))
      {      
         Object instance = lookupRole(role);
         
         if (roleGroupsProperty != null)
         {
            Collection groups = (Collection) roleGroupsProperty.getValue(instance);
            
            if (groups != null)
            {
               for (Object group : groups)
               {
                  addRoleAndMemberships((String) roleNameProperty.getValue(group), roles);
               }
            }
         }
      }
   }
   
   private String generatePasswordHash(String password, String salt)
   {
      String algorithm = userPasswordProperty.getAnnotation().hash();
      
      if (algorithm == null || "".equals(algorithm))
      {
         if (salt == null || "".equals(salt))
         {
            return PasswordHash.instance().generateHash(password);
         }
         else
         {
            return PasswordHash.instance().generateSaltedHash(password, salt);
         }
      }
      else if ("none".equals(algorithm))
      {
         return password;
      }      
      else
      {
         if (salt == null || "".equals(salt))
         {
            return PasswordHash.instance().generateHash(password, algorithm);
         }
         else
         {
            return PasswordHash.instance().generateSaltedHash(password, salt, algorithm);
         }
      }      
   }
   
   public boolean authenticate(String username, String password)
   {
      Object user = lookupUser(username);          
      if (user == null || (userEnabledProperty != null && ((Boolean) userEnabledProperty.getValue(user) == false)))
      {
         return false;
      }
      
      String passwordHash = generatePasswordHash(password, getUserAccountSalt(user)); 
      boolean success = passwordHash.equals(userPasswordProperty.getValue(user));
            
      if (success && Events.exists())
      {
         if (Contexts.isEventContextActive())
         {
            Contexts.getEventContext().set(AUTHENTICATED_USER, user);
         }
         
         Events.instance().raiseEvent(EVENT_USER_AUTHENTICATED, user);
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
   
   public Object lookupUser(String username)       
   {
      try
      {
         Object user = lookupEntityManager().createQuery(
            "select u from " + userClass.getName() + " u where " + userPrincipalProperty.getName() +
            " = :username")
            .setParameter("username", username)
            .getSingleResult();
         
         return user;
      }
      catch (NoResultException ex)
      {
         return null;        
      }      
   }
   
   public String getUserName(Object user)
   {
      return (String) userPrincipalProperty.getValue(user);
   }
   
   public String getRoleName(Object role)
   {
      return (String) roleNameProperty.getValue(role);
   }
   
   public boolean isRoleConditional(String role)
   {      
      return roleConditionalProperty == null ? false : (Boolean) roleConditionalProperty.getValue(
            lookupRole(role));
   }
   
   public Object lookupRole(String role)       
   {
      try
      {
         Object value = lookupEntityManager().createQuery(
            "select r from " + roleClass.getName() + " r where " + roleNameProperty.getName() +
            " = :role")
            .setParameter("role", role)
            .getSingleResult();
         
         return value;
      }
      catch (NoResultException ex)
      {
         return null;        
      }
   }   
   
   public List<String> listUsers()
   {
      return lookupEntityManager().createQuery(
            "select u." + userPrincipalProperty.getName() + " from " + userClass.getName() + " u")
            .getResultList();      
   }
   
   public List<String> listUsers(String filter)
   {
      return lookupEntityManager().createQuery(
            "select u." + userPrincipalProperty.getName() + " from " + userClass.getName() + 
            " u where lower(" + userPrincipalProperty.getName() + ") like :username")
            .setParameter("username", "%" + (filter != null ? filter.toLowerCase() : "") + 
                  "%")
            .getResultList();
   }

   public List<String> listRoles()
   {
      return lookupEntityManager().createQuery(
            "select r." + roleNameProperty.getName() + " from " + roleClass.getName() + " r")
            .getResultList();      
   }   
   
   protected void persistEntity(Object entity)
   {
      lookupEntityManager().persist(entity);
   }
   
   protected Object mergeEntity(Object entity)
   {
      return lookupEntityManager().merge(entity);
   }
   
   protected void removeEntity(Object entity)
   {
      lookupEntityManager().remove(entity);
   }
   
   public Class getUserClass()
   {
      return userClass;
   }
   
   public void setUserClass(Class userClass)
   {
      this.userClass = userClass;
   }   
   
   public Class getRoleClass()
   {
      return roleClass;
   }
   
   public void setRoleClass(Class roleClass)
   {
      this.roleClass = roleClass;
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

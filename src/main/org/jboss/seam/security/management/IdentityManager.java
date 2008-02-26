package org.jboss.seam.security.management;

import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.security.Identity;

/**
 * Identity Management API, deals with user name/password-based identity management.
 * 
 * @author Shane Bryzak
 */
@Scope(APPLICATION)
@Name("org.jboss.seam.security.identityManager")
@Install(precedence = BUILT_IN)
public class IdentityManager
{
   public static final String IDENTITY_STORE_COMPONENT_NAME = "identityStore";
   public static final String ACCOUNT_PERMISSION_NAME = "seam.account";
   
   public static final String PERMISSION_CREATE = "create";
   public static final String PERMISSION_READ = "read";
   public static final String PERMISSION_UPDATE = "update";
   public static final String PERMISSION_DELETE = "delete";
   
   private static final LogProvider log = Logging.getLogProvider(IdentityManager.class);   
   
   private IdentityStore userIdentityStore;
   private IdentityStore roleIdentityStore;
   
   @Create
   public void create()
   {
      initIdentityStore();
   }
   
   protected void initIdentityStore()
   {
      if (userIdentityStore == null)
      {
         userIdentityStore = (IdentityStore) Component.getInstance(IDENTITY_STORE_COMPONENT_NAME, true);
      }
      
      if (roleIdentityStore == null)
      {
         roleIdentityStore = (IdentityStore) Component.getInstance(IDENTITY_STORE_COMPONENT_NAME, true);
      }      

      if (roleIdentityStore == null && userIdentityStore != null)
      {
         roleIdentityStore = userIdentityStore;
      }            
      
      if (userIdentityStore == null || roleIdentityStore == null)
      {
         log.warn("no identity store available - please install an IdentityStore with the name '" +
               IDENTITY_STORE_COMPONENT_NAME + "' if identity management is required.");
      }
   }  
   
   public static IdentityManager instance()
   {
      if ( !Contexts.isApplicationContextActive() )
      {
         throw new IllegalStateException("No active application context");
      }

      IdentityManager instance = (IdentityManager) Component.getInstance(
            IdentityManager.class, ScopeType.APPLICATION);

      if (instance == null)
      {
         throw new IllegalStateException("No IdentityManager could be created");
      }

      return instance;
   }
   
   public boolean createUser(String name, String password)
   {
      Identity.instance().checkPermission(ACCOUNT_PERMISSION_NAME, PERMISSION_CREATE);
      return userIdentityStore.createUser(name, password); 
   }
   
   public boolean deleteUser(String name)
   {
      Identity.instance().checkPermission(ACCOUNT_PERMISSION_NAME, PERMISSION_DELETE);
      return userIdentityStore.deleteUser(name);
   }
   
   public boolean enableUser(String name)
   {
      Identity.instance().checkPermission(ACCOUNT_PERMISSION_NAME, PERMISSION_UPDATE);
      return userIdentityStore.enableUser(name);
   }
   
   public boolean disableUser(String name)
   {
      Identity.instance().checkPermission(ACCOUNT_PERMISSION_NAME, PERMISSION_UPDATE);
      return userIdentityStore.disableUser(name);
   }
   
   public boolean changePassword(String name, String password)
   {
      Identity.instance().checkPermission(ACCOUNT_PERMISSION_NAME, PERMISSION_UPDATE);
      return userIdentityStore.changePassword(name, password);
   }
   
   public boolean isUserEnabled(String name)
   {
      Identity.instance().checkPermission(ACCOUNT_PERMISSION_NAME, PERMISSION_READ);
      return userIdentityStore.isUserEnabled(name);
   }
   
   public boolean grantRole(String name, String role)
   {
      Identity.instance().checkPermission(ACCOUNT_PERMISSION_NAME, PERMISSION_UPDATE);
      return roleIdentityStore.grantRole(name, role);
   }
   
   public boolean revokeRole(String name, String role)
   {
      Identity.instance().checkPermission(ACCOUNT_PERMISSION_NAME, PERMISSION_UPDATE);
      return roleIdentityStore.revokeRole(name, role);
   }
   
   public boolean createRole(String role)
   {
      Identity.instance().checkPermission(ACCOUNT_PERMISSION_NAME, PERMISSION_CREATE);
      return roleIdentityStore.createRole(role);
   }
   
   public boolean deleteRole(String role)
   {
      Identity.instance().checkPermission(ACCOUNT_PERMISSION_NAME, PERMISSION_DELETE);
      return roleIdentityStore.deleteRole(role);
   }
   
   public boolean userExists(String name)
   {
      Identity.instance().checkPermission(ACCOUNT_PERMISSION_NAME, PERMISSION_READ);
      return userIdentityStore.userExists(name);
   }
   
   public boolean roleExists(String name)
   {
      return roleIdentityStore.roleExists(name);      
   }
   
   public List<String> listUsers()
   {
      Identity.instance().checkPermission(ACCOUNT_PERMISSION_NAME, PERMISSION_READ);
      List<String> users = userIdentityStore.listUsers();      
      
      Collections.sort(users, new Comparator<String>() {
         public int compare(String value1, String value2) {
            return value1.compareTo(value2);
         }
      });
      
      return users;
   }
   
   public List<String> listUsers(String filter)
   {
      Identity.instance().checkPermission(ACCOUNT_PERMISSION_NAME, PERMISSION_READ);
      List<String> users = userIdentityStore.listUsers(filter);
      
      Collections.sort(users, new Comparator<String>() {
         public int compare(String value1, String value2) {
            return value1.compareTo(value2);
         }
      });
      
      return users;      
   }
   
   public List<String> listRoles()
   {      
      List<String> roles = roleIdentityStore.listRoles();
      
      Collections.sort(roles, new Comparator<String>() {
         public int compare(String value1, String value2) {
            return value1.compareTo(value2);
         }
      });
      
      return roles;      
   }
   
   public List<String> getGrantedRoles(String name)
   {
      return roleIdentityStore.getGrantedRoles(name);
   }
   
   public List<String> getImpliedRoles(String name)
   {
      return roleIdentityStore.getImpliedRoles(name);
   }
   
   public boolean authenticate(String username, String password)
   {
      return userIdentityStore.authenticate(username, password);
   }
   
   public IdentityStore getUserIdentityStore()
   {
      return userIdentityStore;
   }
   
   public void setIdentityStore(IdentityStore userIdentityStore)
   {
      this.userIdentityStore = userIdentityStore;
   }
   
   public IdentityStore getRoleIdentityStore()
   {
      return roleIdentityStore;
   }
   
   public void setRoleIdentityStore(IdentityStore roleIdentityStore)
   {
      this.roleIdentityStore = roleIdentityStore;
   }
   
   public boolean isEnabled()
   {
      return userIdentityStore != null && roleIdentityStore != null;
   }
   
}

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
   
   private static final LogProvider log = Logging.getLogProvider(IdentityManager.class);   
   
   private IdentityStore identityStore;   
   
   @Create
   public void create()
   {
      initIdentityStore();
   }
   
   protected void initIdentityStore()
   {
      if (identityStore == null)
      {
         identityStore = (IdentityStore) Component.getInstance(IDENTITY_STORE_COMPONENT_NAME, true);
      }
      
      if (identityStore == null)
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
   
   public boolean createAccount(String name, String password)
   {
      Identity.instance().checkPermission(ACCOUNT_PERMISSION_NAME, "create");
      return identityStore.createAccount(name, password); 
   }
   
   public boolean deleteAccount(String name)
   {
      Identity.instance().checkPermission(ACCOUNT_PERMISSION_NAME, "delete");
      return identityStore.deleteAccount(name);
   }
   
   public boolean enableAccount(String name)
   {
      Identity.instance().checkPermission(ACCOUNT_PERMISSION_NAME, "update");
      return identityStore.enableAccount(name);
   }
   
   public boolean disableAccount(String name)
   {
      Identity.instance().checkPermission(ACCOUNT_PERMISSION_NAME, "update");
      return identityStore.disableAccount(name);
   }
   
   public boolean changePassword(String name, String password)
   {
      Identity.instance().checkPermission(ACCOUNT_PERMISSION_NAME, "update");
      return identityStore.changePassword(name, password);
   }
   
   public boolean isEnabled(String name)
   {
      Identity.instance().checkPermission(ACCOUNT_PERMISSION_NAME, "read");
      return identityStore.isEnabled(name);
   }
   
   public boolean grantRole(String name, String role)
   {
      Identity.instance().checkPermission(ACCOUNT_PERMISSION_NAME, "update");
      return identityStore.grantRole(name, role);
   }
   
   public boolean revokeRole(String name, String role)
   {
      Identity.instance().checkPermission(ACCOUNT_PERMISSION_NAME, "update");
      return identityStore.revokeRole(name, role);
   }
   
   public boolean accountExists(String name)
   {
      Identity.instance().checkPermission(ACCOUNT_PERMISSION_NAME, "read");
      return identityStore.accountExists(name);
   }
   
   public List<String> listUsers()
   {
      Identity.instance().checkPermission(ACCOUNT_PERMISSION_NAME, "read");
      List<String> users = identityStore.listUsers();      
      
      Collections.sort(users, new Comparator<String>() {
         public int compare(String value1, String value2) {
            return value1.compareTo(value2);
         }
      });
      
      return users;
   }
   
   public List<String> listUsers(String filter)
   {
      Identity.instance().checkPermission(ACCOUNT_PERMISSION_NAME, "read");
      List<String> users = identityStore.listUsers(filter);
      
      Collections.sort(users, new Comparator<String>() {
         public int compare(String value1, String value2) {
            return value1.compareTo(value2);
         }
      });
      
      return users;      
   }
   
   public List<String> listRoles()
   {      
      List<String> roles = identityStore.listRoles();
      
      Collections.sort(roles, new Comparator<String>() {
         public int compare(String value1, String value2) {
            return value1.compareTo(value2);
         }
      });
      
      return roles;      
   }
   
   public List<String> getGrantedRoles(String name)
   {
      return identityStore.getGrantedRoles(name);
   }
   
   public List<String> getImpliedRoles(String name)
   {
      return identityStore.getImpliedRoles(name);
   }
   
   public boolean authenticate(String username, String password)
   {
      return identityStore.authenticate(username, password);
   }
   
   public IdentityStore getIdentityStore()
   {
      return identityStore;
   }
   
   public void setIdentityStore(IdentityStore identityStore)
   {
      this.identityStore = identityStore;
   }
   
}

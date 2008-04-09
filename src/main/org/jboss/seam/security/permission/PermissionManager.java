package org.jboss.seam.security.permission;

import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.Serializable;
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
import org.jboss.seam.security.permission.acl.AclPermission;
import org.jboss.seam.security.permission.acl.AclPermissionStore;
import org.jboss.seam.security.permission.dynamic.AccountPermission;
import org.jboss.seam.security.permission.dynamic.AccountPermissionStore;

/**
 * Permission management component, used to grant or revoke permissions on specific objects or of
 * specific permission types to particular users or roles.
 * 
 * @author Shane Bryzak
 */
@Scope(APPLICATION)
@Name("org.jboss.seam.security.permissionManager")
@Install(precedence = BUILT_IN)
public class PermissionManager implements Serializable
{
   public static final String ACCOUNT_PERMISSION_STORE_COMPONENT_NAME = "accountPermissionStore";
   public static final String ACL_PERMISSION_STORE_COMPONENT_NAME = "aclPermissionStore";
   
   public static final String PERMISSION_PERMISSION_NAME = "seam.permission";
   
   public static final String PERMISSION_READ = "read";
   public static final String PERMISSION_GRANT = "grant";
   public static final String PERMISSION_REVOKE = "revoke";   
   
   private static final LogProvider log = Logging.getLogProvider(PermissionManager.class);
   
   private AccountPermissionStore accountPermissionStore;
   
   private AclPermissionStore aclPermissionStore;
   
   @Create
   public void create()
   {
      if (accountPermissionStore == null)
      {
         accountPermissionStore = (AccountPermissionStore) Component.getInstance(ACCOUNT_PERMISSION_STORE_COMPONENT_NAME, true);
      }         
      
      if (accountPermissionStore == null)
      {
         log.warn("no account permission store available - please install an AccountPermissionStore with the name '" +
               ACCOUNT_PERMISSION_STORE_COMPONENT_NAME + "' if account-based permission management is required.");
      }
      
      if (aclPermissionStore == null)
      {
         aclPermissionStore = (AclPermissionStore) Component.getInstance(ACL_PERMISSION_STORE_COMPONENT_NAME);
      }
      
      if (aclPermissionStore == null)
      {
         log.warn("no ACL permission store available - please install an AclPermissionStore with the name '" +
               ACL_PERMISSION_STORE_COMPONENT_NAME + "' if ACL-based permission management is required.");
      }
   } 
   
   public static PermissionManager instance()
   {
      if ( !Contexts.isApplicationContextActive() )
      {
         throw new IllegalStateException("No active application context");
      }

      PermissionManager instance = (PermissionManager) Component.getInstance(
            PermissionManager.class, ScopeType.APPLICATION);

      if (instance == null)
      {
         throw new IllegalStateException("No PermissionManager could be created");
      }

      return instance;
   }
   
   public AccountPermissionStore getAccountPermissionStore()
   {
      return accountPermissionStore;
   }
   
   public void setAccountPermissionStore(AccountPermissionStore accountPermissionStore)
   {
      this.accountPermissionStore = accountPermissionStore;
   }
   
   public List<AccountPermission> listPermissions(String target, String action)
   {
      Identity.instance().checkPermission(PERMISSION_PERMISSION_NAME, PERMISSION_READ);
      return accountPermissionStore.listPermissions(target, action);
   }
   
   public List<AccountPermission> listPermissions(String target)
   {
      Identity.instance().checkPermission(PERMISSION_PERMISSION_NAME, PERMISSION_READ);
      return accountPermissionStore.listPermissions(target);
   }
   
   public List<AclPermission> listPermissions(Object target)
   {
      Identity.instance().checkPermission(PERMISSION_PERMISSION_NAME, PERMISSION_READ);
      return aclPermissionStore.listPermissions(target);
   }
   
   public boolean grantPermission(String target, String action, String account, AccountType accountType)
   {
      Identity.instance().checkPermission(PERMISSION_PERMISSION_NAME, PERMISSION_GRANT);
      return accountPermissionStore.grantPermission(target, action, account, accountType);
   }
   
   public boolean grantPermission(Object target, String action, String account, AccountType accountType)
   {
      Identity.instance().checkPermission(PERMISSION_PERMISSION_NAME, PERMISSION_GRANT);
      return aclPermissionStore.grantPermission(target, action, account, accountType);
   }
   
   public boolean revokePermission(String target, String action, String account, AccountType accountType)
   {
      Identity.instance().checkPermission(PERMISSION_PERMISSION_NAME, PERMISSION_REVOKE);
      return accountPermissionStore.revokePermission(target, action, account, accountType);
   }
   
   public boolean revokePermission(Object target, String action, String account, AccountType accountType)
   {
      Identity.instance().checkPermission(PERMISSION_PERMISSION_NAME, PERMISSION_REVOKE);
      return aclPermissionStore.revokePermission(target, action, account, accountType);
   }
}

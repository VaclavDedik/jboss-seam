package org.jboss.seam.security.permission;

import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.FRAMEWORK;

import java.io.Serializable;
import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.security.Identity;

@Name("org.jboss.seam.security.aclPermissionResolver")
@Scope(APPLICATION)
@BypassInterceptors
@Install(precedence=FRAMEWORK)
@Startup
public class AclPermissionResolver implements PermissionResolver, Serializable
{
   private static final String DEFAULT_PERMISSION_STORE_NAME = "aclPermissionStore";
   
   private AclPermissionStore permissionStore;
   
   private static final LogProvider log = Logging.getLogProvider(AclPermissionResolver.class);   
   
   @Create
   public void create()
   {
      initPermissionStore();
   }
   
   protected void initPermissionStore()
   {
      if (permissionStore == null)
      {
         permissionStore = (AclPermissionStore) Component.getInstance(DEFAULT_PERMISSION_STORE_NAME, true);
      }           
      
      if (permissionStore == null)
      {
         log.warn("no permission store available - please install a PermissionStore with the name '" +
               DEFAULT_PERMISSION_STORE_NAME + "' if acl-based permissions are required.");
      }
   } 
   
   public boolean hasPermission(Object target, String action)
   {
      if (permissionStore == null) return false;
      
      List<AclPermission> permissions = permissionStore.listPermissions(target);
      
      Identity identity = Identity.instance();
      
      if (!identity.isLoggedIn()) return false;
      
      String username = identity.getPrincipal().getName();

      for (AclPermission permission : permissions)
      {
         if ((username.equals(permission.getAccount()) && permission.getAccountType().equals(AccountType.user)) ||
             (permission.getAccountType().equals(AccountType.role) && identity.hasRole(permission.getAccount())))
         {
            if (hasPermissionFlag(target, action, permission.getPermissions()))
            {
               return true;
            }
         }         
      }
      
      return false;
   }
   
   protected boolean hasPermissionFlag(Object target, String action, long permissions)
   {
      // TODO
      
      return false;
   }

}

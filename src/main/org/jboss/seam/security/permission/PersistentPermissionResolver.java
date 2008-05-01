package org.jboss.seam.security.permission;

import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.FRAMEWORK;

import java.io.Serializable;
import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.Role;
import org.jboss.seam.security.SimplePrincipal;

/**
 * Resolves dynamically-assigned permissions, mapped to a user or a role, and kept in persistent 
 * storage, such as a relational database.
 * 
 * @author Shane Bryzak
 */
@Name("org.jboss.seam.security.persistentPermissionResolver")
@Scope(APPLICATION)
@BypassInterceptors
@Install(precedence=FRAMEWORK)
@Startup
public class PersistentPermissionResolver implements PermissionResolver, Serializable
{      
   private PermissionStore permissionStore;
   
   private IdentifierPolicy identifierPolicy;
   
   private static final LogProvider log = Logging.getLogProvider(PersistentPermissionResolver.class);   
   
   @Create
   public void create()
   {
      initPermissionStore();
      
      identifierPolicy = (IdentifierPolicy) Component.getInstance(IdentifierPolicy.class, true);
   }
   
   protected void initPermissionStore()
   {
      if (permissionStore == null)
      {
         permissionStore = (PermissionStore) Component.getInstance(JpaPermissionStore.class, true);
      }           
      
      if (permissionStore == null)
      {
         log.warn("no permission store available - please install a PermissionStore with the name '" +
               Seam.getComponentName(JpaPermissionStore.class) + "' if persistent permissions are required.");
      }
   }     
   
   public PermissionStore getPermissionStore()
   {
      return permissionStore;
   }
   
   public void setPermissionStore(PermissionStore permissionStore)
   {
      this.permissionStore = permissionStore;
   }
   
   public boolean hasPermission(Object target, String action)
   {      
      if (permissionStore == null) return false;
      
      Identity identity = Identity.instance();
      
      if (!identity.isLoggedIn()) return false;
      
      String identifier = identifierPolicy.getIdentifier(target);
      
      List<Permission> permissions = permissionStore.listPermissions(identifier, action);
      
      String username = identity.getPrincipal().getName();
      
      for (Permission permission : permissions)
      {
         if (permission.getRecipient() instanceof SimplePrincipal &&
               username.equals(permission.getRecipient().getName()))
         {
            return true;
         }
         
         if (permission.getRecipient() instanceof Role)
         {
            Role role = (Role) permission.getRecipient();
            
            if (role.isDynamic())
            {
               // TODO implement dynamic permissions
            }
            else if (identity.hasRole(role.getName()))
            {
               return true;
            }
         }
      }      
      
      return false;
   }
}

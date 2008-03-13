package org.jboss.seam.security.permission;

import java.util.List;

import org.jboss.seam.security.permission.AccountPermission.AccountType;

/**
 * Persistent store for user/role permissions
 *  
 * @author Shane Bryzak
 */
public interface PermissionStore
{
   List<AccountPermission> listPermissions(String target, String action);
   List<AccountPermission> listPermissions(String target);
   
   boolean grantPermission(String target, String action, String account, AccountType accountType);
   boolean revokePermission(String target, String action, String account, AccountType accountType);
}

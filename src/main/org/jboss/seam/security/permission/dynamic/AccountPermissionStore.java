package org.jboss.seam.security.permission.dynamic;

import java.util.List;

import org.jboss.seam.security.permission.AccountType;

/**
 * Persistent store for account-based (user/role) permissions
 *  
 * @author Shane Bryzak
 */
public interface AccountPermissionStore
{
   List<AccountPermission> listPermissions(String target, String action);
   List<AccountPermission> listPermissions(String target);
   
   boolean grantPermission(String target, String action, String account, AccountType accountType);
   boolean revokePermission(String target, String action, String account, AccountType accountType);
}

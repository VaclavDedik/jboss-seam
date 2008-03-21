package org.jboss.seam.security.permission;

import java.util.List;

/**
 * Persistent storage for ACL (instance-based) permissions
 * 
 * @author Shane Bryzak
 */
public interface AclPermissionStore
{
   List<AclPermission> listPermissions(Object target);
   boolean grantPermission(Object target, String action, String account, AccountType accountType);
   boolean revokePermission(Object target, String action, String account, AccountType accountType);
}

package org.jboss.seam.security.permission;

import java.util.List;

/**
 * Permission store interface.
 * 
 * @author Shane Bryzak
 */
public interface PermissionStore
{
   List<Permission> listPermissions(Object target);
   List<Permission> listPermissions(Object target, String action);
   boolean grantPermission(Permission permission);
   boolean revokePermission(Permission permission);
   List<String> listAvailableActions(Object target);
}

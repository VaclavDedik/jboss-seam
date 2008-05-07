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
   boolean grantPermissions(List<Permission> permissions);
   boolean revokePermission(Permission permission);
   boolean revokePermissions(List<Permission> permissions);
   List<String> listAvailableActions(Object target);
}

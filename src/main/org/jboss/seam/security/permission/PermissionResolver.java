package org.jboss.seam.security.permission;

public interface PermissionResolver
{
   boolean hasPermission(Object target, String action);
}

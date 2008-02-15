package org.jboss.seam.security;

public interface PermissionResolver
{
   boolean hasPermission(Object target, String action);
}

package org.jboss.seam.security.permission;

/**
 * Implementations of this interface perform permission checks using a variety of methods.
 *  
 * @author Shane Bryzak
 */
public interface PermissionResolver
{
   boolean hasPermission(Object target, String action);
}

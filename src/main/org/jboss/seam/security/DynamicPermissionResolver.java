package org.jboss.seam.security;

/**
 * Resolves permissions dynamically assigned in a peristent store, such as a 
 * database, for example.
 * 
 * @author Shane Bryzak
 */
public class DynamicPermissionResolver implements PermissionResolver
{   
   public boolean hasPermission(Object target, String action)
   {
      return true;  
   }
}

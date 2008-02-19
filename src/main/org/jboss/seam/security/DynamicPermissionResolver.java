package org.jboss.seam.security;

import java.io.Serializable;

/**
 * Resolves permissions dynamically assigned in a peristent store, such as a 
 * database, for example.
 * 
 * @author Shane Bryzak
 */
public class DynamicPermissionResolver implements PermissionResolver, Serializable
{   
   public boolean hasPermission(Object target, String action)
   {
      return true;  
   }
}

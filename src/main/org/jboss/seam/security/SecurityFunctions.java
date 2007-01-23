package org.jboss.seam.security;

/**
 * Delegating wrapper for EL security functions.
 * 
 * @author Shane Bryzak
 */
public class SecurityFunctions
{
   public static boolean hasRole(String name)
   {
      return Security.instance().hasRole(name);
   }
   
   public static boolean hasPermission(String name, String action,
            Object... args)
   {
      return Security.instance().hasPermission(name, action, args);
   }
}

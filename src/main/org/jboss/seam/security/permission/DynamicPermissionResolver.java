package org.jboss.seam.security.permission;

import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.FRAMEWORK;

import java.io.Serializable;

import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

/**
 * Resolves permissions dynamically assigned in a persistent store, such as a 
 * database, for example.
 * 
 * @author Shane Bryzak
 */
@Name("org.jboss.seam.security.dynamicPermissionResolver")
@Scope(APPLICATION)
@BypassInterceptors
@Install(precedence=FRAMEWORK)
@Startup
public class DynamicPermissionResolver implements PermissionResolver, Serializable
{   
   public boolean hasPermission(Object target, String action)
   {
      return false;  
   }
}

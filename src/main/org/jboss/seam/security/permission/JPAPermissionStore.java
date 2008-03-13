package org.jboss.seam.security.permission;

import static org.jboss.seam.ScopeType.APPLICATION;

import java.util.List;

import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.security.permission.AccountPermission.AccountType;

/**
 * A permission store implementation that uses JPA as its persistence mechanism.
 * 
 * @author Shane Bryzak
 */
@Scope(APPLICATION)
@BypassInterceptors
public class JPAPermissionStore implements PermissionStore
{
   public boolean grantPermission(String target, String action, String account,
         AccountType accountType) 
   {
      return false;
   }

   public List<AccountPermission> listPermissions(String target, String action) 
   {
      return null;
   }

   public List<AccountPermission> listPermissions(String target) 
   {
      return null;
   }

   public boolean revokePermission(String target, String action,
         String account, AccountType accountType) 
   {
      return false;
   }

}

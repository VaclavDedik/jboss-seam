package org.jboss.seam.security.management;

import java.util.List;

/**
 * The identity store does the actual work of persisting user accounts in a
 * database, LDAP directory, etc.  
 * 
 * @author Shane Bryzak
 */
public abstract class IdentityStore
{     
   protected abstract boolean createAccount(String username, String password);
   protected abstract boolean deleteAccount(String name);
   
   protected abstract boolean grantRole(String name, String role);
   protected abstract boolean revokeRole(String name, String role);
   
   protected abstract boolean enableAccount(String name);
   protected abstract boolean disableAccount(String name);   
   
   protected abstract List<String> listUsers();
   protected abstract List<String> listUsers(String filter);
   protected abstract List<String> listRoles();
   
   protected abstract List<String> getGrantedRoles(String name);
   protected abstract List<String> getImpliedRoles(String name);
   
   protected abstract boolean authenticate(String username, String password);
}

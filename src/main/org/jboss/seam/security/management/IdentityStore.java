package org.jboss.seam.security.management;

import java.util.List;

/**
 * The identity store does the actual work of persisting user accounts in a
 * database, LDAP directory, etc.  
 * 
 * @author Shane Bryzak
 */
public interface IdentityStore
{     
   boolean createAccount(String username, String password);
   boolean deleteAccount(String name);
   
   boolean grantRole(String name, String role);
   boolean revokeRole(String name, String role);
   
   boolean enableAccount(String name);
   boolean disableAccount(String name);   
   boolean isEnabled(String name);
   boolean changePassword(String name, String password);
   
   boolean accountExists(String name);
   List<String> listUsers();
   List<String> listUsers(String filter);
   List<String> listRoles();
   
   List<String> getGrantedRoles(String name);
   List<String> getImpliedRoles(String name);
   
   boolean authenticate(String username, String password);
}

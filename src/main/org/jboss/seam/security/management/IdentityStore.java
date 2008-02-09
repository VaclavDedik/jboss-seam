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
   boolean createUser(String username, String password);
   boolean deleteUser(String name);   
   boolean enableUser(String name);
   boolean disableUser(String name);   
   boolean isUserEnabled(String name);
   boolean changePassword(String name, String password);   
   boolean userExists(String name);
   
   boolean createRole(String role);
   boolean grantRole(String name, String role);
   boolean revokeRole(String name, String role);
   boolean deleteRole(String role);
   boolean roleExists(String name);   

   List<String> listUsers();
   List<String> listUsers(String filter);
   List<String> listRoles();
   
   List<String> getGrantedRoles(String name);
   List<String> getImpliedRoles(String name);
   
   boolean authenticate(String username, String password);
}

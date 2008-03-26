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
   public class FeatureSet 
   {
      public static final int FEATURE_CREATE_USER = 1;
      public static final int FEATURE_DELETE_USER = 2;
      public static final int FEATURE_ENABLE_USER = 4;
      public static final int FEATURE_DISABLE_USER = 8;      
      public static final int FEATURE_CHANGE_PASSWORD = 16;
      
      public static final int FEATURE_CREATE_ROLE = 32;
      public static final int FEATURE_DELETE_ROLE = 64;
      public static final int FEATURE_GRANT_ROLE = 128;
      public static final int FEATURE_REVOKE_ROLE = 256;
      
      public static final int FEATURE_ALL_USER = FEATURE_CREATE_USER | 
          FEATURE_DELETE_USER | 
          FEATURE_ENABLE_USER |
          FEATURE_DISABLE_USER |
          FEATURE_CHANGE_PASSWORD;
      
      public static final int FEATURE_ALL_ROLE = FEATURE_CREATE_ROLE |
          FEATURE_DELETE_ROLE |
          FEATURE_GRANT_ROLE |
          FEATURE_REVOKE_ROLE;
          
      public static final int FEATURE_ALL = FEATURE_ALL_USER | FEATURE_ALL_ROLE;
      
      private int features;
      
      public FeatureSet(int features)
      {
         this.features = features;
      }
      
      public int getFeatures()
      {
         return features;
      }
      
      public boolean supports(int feature)
      {
         return (features & feature) == feature;
      }
   }
   
   boolean supportsFeature(int feature);
   
   boolean createUser(String username, String password);
   boolean createUser(String username, String password, String firstname, String lastname);
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

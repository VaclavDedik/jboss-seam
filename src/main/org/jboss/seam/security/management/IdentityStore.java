package org.jboss.seam.security.management;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The identity store does the actual work of persisting user accounts in a
 * database, LDAP directory, etc.  
 * 
 * @author Shane Bryzak
 */
public interface IdentityStore
{     
   public enum Feature { createUser, deleteUser, enableUser, disableUser, changePassword, 
      createRole, deleteRole, grantRole, revokeRole }
   
   public class FeatureSet 
   {                             
      private Set<Feature> features;

      public FeatureSet()
      {
         this(null);
      }
      
      public FeatureSet(Set<Feature> features)
      {
         if (features != null)
         {
            this.features = features;
         }
         else
         {
            this.features = new HashSet<Feature>();
         }
      }
      
      public Set<Feature> getFeatures()
      {
         return features;
      }
      
      public boolean supports(Feature feature)
      {
         return features.contains(feature);
      }
      
      public void addFeature(Feature feature)
      {
         features.add(feature);
      }
      
      public void removeFeature(Feature feature)
      {
         features.remove(feature);
      }
      
      public void enableAll()
      {
         for (Feature f : Feature.values()) addFeature(f);
      }
   }
   
   boolean supportsFeature(Feature feature);
   
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
   boolean addRoleToGroup(String role, String group);
   boolean removeRoleFromGroup(String role, String group);   

   List<String> listUsers();
   List<String> listUsers(String filter);
   List<String> listRoles();
   
   List<String> getGrantedRoles(String name);
   List<String> getImpliedRoles(String name);
   List<String> getRoleGroups(String name);
   
   boolean authenticate(String username, String password);
}

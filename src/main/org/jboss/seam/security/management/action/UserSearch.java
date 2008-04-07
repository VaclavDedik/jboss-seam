package org.jboss.seam.security.management.action;

import static org.jboss.seam.ScopeType.SESSION;

import java.io.Serializable;
import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Synchronized;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;

import org.jboss.seam.security.management.IdentityManager;

@Name("userSearch")
@Scope(SESSION)
@Synchronized
public class UserSearch implements Serializable
{
   private static final long serialVersionUID = 8592034786339372510L;

   @DataModel
   List<String> users;
   
   @DataModelSelection
   String selectedUser;
   
   @In IdentityManager identityManager;
   
   public void loadUsers()
   {
      users = identityManager.listUsers();     
   }
   
   public String getUserRoles(String username)
   {
      List<String> roles = identityManager.getGrantedRoles(username);
      
      if (roles == null) return "";
      
      StringBuilder sb = new StringBuilder();
      
      for (String role : roles)
      {
         sb.append((sb.length() > 0 ? ", " : "") + role); 
      }
      
      return sb.toString();      
   }
   
   public String getSelectedUser()
   {
      return selectedUser;
   }
}
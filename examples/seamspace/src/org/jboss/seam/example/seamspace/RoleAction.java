package org.jboss.seam.example.seamspace;

import static org.jboss.seam.ScopeType.CONVERSATION;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.security.management.IdentityManager;

@Name("roleAction")
@Scope(CONVERSATION)
public class RoleAction
{
   private String role;
   private List<String> memberships;
   
   @In IdentityManager identityManager;
   
   @Begin
   public void createRole()
   {
      memberships = new ArrayList<String>();
   }
   
   @Begin
   public void editRole(String role)
   {
      this.role = role;
      memberships = identityManager.getGrantedRoles(role);
   }
      
   public String save()
   {
      if (identityManager.roleExists(role))
      {
         return saveExistingRole();
      }
      else
      {
         return saveNewRole();
      }
   }
   
   private String saveNewRole()
   {      
      boolean success = identityManager.createRole(role);
      
      if (success)
      {
         for (String r : memberships)
         {
            identityManager.grantRole(role, r);
         }
         
         Conversation.instance().end();
      }
      
      return "success";      
   }
   
   private String saveExistingRole()
   {
      List<String> grantedRoles = identityManager.getGrantedRoles(role);
      
      if (grantedRoles != null)
      {
         for (String r : grantedRoles)
         {
            if (!memberships.contains(r)) identityManager.revokeRole(role, r);
         }
      }
      
      for (String r : memberships)
      {
         if (grantedRoles == null || !grantedRoles.contains(r)) identityManager.grantRole(role, r);
      }
               
      Conversation.instance().end();
      return "success";
   }
   
   public String getRole()
   {
      return role;
   }
   
   public void setRole(String role)
   {
      this.role = role;
   }

   public List<String> getMemberships()
   {
      return memberships;
   }
   
   public void setMemberships(List<String> memberships)
   {
      this.memberships = memberships;
   }
}
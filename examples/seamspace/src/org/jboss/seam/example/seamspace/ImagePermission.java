package org.jboss.seam.example.seamspace;

import static org.jboss.seam.ScopeType.CONVERSATION;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.security.management.IdentityManager;
import org.jboss.seam.security.permission.PermissionManager;
import org.jboss.seam.security.permission.action.PermissionSearch;

@Name("imagePermission")
@Scope(CONVERSATION)
public class ImagePermission implements Serializable
{
   private static final long serialVersionUID = -4943654157860780587L;

   private List<String> selectedRoles;   
   private List<Member> selectedFriends;
   private List<String> selectedActions;
   
   private List<String> availableRoles;
   private List<Member> availableFriends;   
   private List<String> availableActions;
   
   @In IdentityManager identityManager;
   @In PermissionManager permissionManager;
   
   @In EntityManager entityManager;
   
   @In PermissionSearch permissionSearch;   
   
   private MemberImage target; 
   
   @SuppressWarnings("unchecked")
   @Begin(nested = true)
   public void createPermission()
   {
      target = (MemberImage) permissionSearch.getTarget();
      
      availableRoles = identityManager.listRoles();
      availableFriends = entityManager.createQuery(
            "select f.friend from MemberFriend f where f.member = :member and f.authorized = true")
            .setParameter("member", target.getMember())
            .getResultList();
      
      availableActions = permissionManager.listAvailableActions(target); 
   }

   public List<String> getSelectedRoles()
   {
      return selectedRoles;
   }
   
   public void setSelectedRoles(List<String> selectedRoles)
   {
      this.selectedRoles = selectedRoles;
   }
   
   public List<Member> getSelectedFriends()
   {
      return selectedFriends;
   }
   
   public void setSelectedFriends(List<Member> selectedFriends)
   {
      this.selectedFriends = selectedFriends;
   }
   
   public List<String> getSelectedActions()
   {
      return selectedActions;
   }
   
   public void setSelectedActions(List<String> selectedActions)
   {
      this.selectedActions = selectedActions;
   }
   
   public void applyPermissions()
   {
      
      
      Conversation.instance().end();
   }
   
   public List<String> getAvailableRoles()
   {
      return availableRoles;
   }
   
   public List<Member> getAvailableFriends()
   {
      return availableFriends;
   }
   
   public List<String> getAvailableActions()
   {
      return availableActions;
   }
}

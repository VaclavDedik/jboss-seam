package @actionPackage@;

import static org.jboss.seam.ScopeType.CONVERSATION;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.security.management.IdentityManager;

@Name("userAction")
@Scope(CONVERSATION)
public class UserAction
{
   private String username;
   private String password;
   private String confirm;
   private List<String> roles;
   private boolean enabled;
   
   @In IdentityManager identityManager;
   
   @Begin
   public void createUser()
   {
      roles = new ArrayList<String>();
   }
   
   @Begin
   public void editUser(String username)
   {
      this.username = username;
      roles = identityManager.getGrantedRoles(username);
      enabled = identityManager.isEnabled(username);
   }
      
   public String save()
   {
      if (identityManager.accountExists(username))
      {
         return saveExistingUser();
      }
      else
      {
         return saveNewUser();
      }
   }
   
   private String saveNewUser()
   {      
      if (!password.equals(confirm))
      {
         FacesMessages.instance().addToControl("password", "Passwords do not match");
         return "failure";
      }
      
      boolean success = identityManager.createAccount(username, password);
      
      if (success)
      {
         for (String role : roles)
         {
            identityManager.grantRole(username, role);
         }
         
         if (!enabled)
         {
            identityManager.disableAccount(username);   
         }
         
         Conversation.instance().end();
      }
      
      return "success";      
   }
   
   private String saveExistingUser()
   {
      // Check if a new password has been entered
      if (password != null && !"".equals(password))
      {
         if (!password.equals(confirm))
         {
            FacesMessages.instance().addToControl("password", "Passwords do not match");
            return "failure";
         }
         else
         {
            identityManager.changePassword(username, password);
         }
      }
      
      List<String> grantedRoles = identityManager.getGrantedRoles(username);
      
      for (String role : grantedRoles)
      {
         if (!roles.contains(role)) identityManager.revokeRole(username, role);
      }
      
      for (String role : roles)
      {
         if (!grantedRoles.contains(role)) identityManager.grantRole(username, role);
      }
      
      if (enabled)
      {
         identityManager.enableAccount(username);
      }
      else
      {
         identityManager.disableAccount(username);
      }
         
      Conversation.instance().end();
      return "success";
   }
   
   public String getUsername()
   {
      return username;
   }
   
   public void setUsername(String username)
   {
      this.username = username;
   }
   
   public String getPassword()
   {
      return password;
   }
   
   public void setPassword(String password)
   {
      this.password = password;
   }
   
   public String getConfirm()
   {
      return confirm;
   }
   
   public void setConfirm(String confirm)
   {
      this.confirm = confirm;
   }
   
   public List<String> getRoles()
   {
      return roles;
   }
   
   public void setRoles(List<String> roles)
   {
      this.roles = roles;
   }
   
   public boolean isEnabled()
   {
      return enabled;
   }
   
   public void setEnabled(boolean enabled)
   {
      this.enabled = enabled;
   }
}

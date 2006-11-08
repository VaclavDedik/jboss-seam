//$Id$
package org.jboss.seam.example.hibernate;

import org.hibernate.Session;
import static org.jboss.seam.ScopeType.EVENT;

import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.FacesMessages;

@Scope(EVENT)
@Name("changePassword")
// @LoggedIn
public class ChangePasswordAction {

   @In @Out
   private User user;
   
   @In (create=true)
   private Session bookingDatabase;
   
   private String verify;
   
   public String changePassword()
   {
      if ( user.getPassword().equals(verify) )
      {
         user = (User) bookingDatabase.merge(user);
         FacesMessages.instance().add("Password updated");
         return "main";
      }
      else 
      {
         FacesMessages.instance().add("verify", "Re-enter new password");
         revertUser();
         verify=null;
         return null;
      }
   }
   
   public String cancel()
   {
      revertUser();
      return "main";
   }

   private void revertUser()
   {
      user = (User) bookingDatabase.get(User.class, user.getUsername());
   }

   public String getVerify()
   {
      return verify;
   }

   public void setVerify(String verify)
   {
      this.verify = verify;
   }
   
}

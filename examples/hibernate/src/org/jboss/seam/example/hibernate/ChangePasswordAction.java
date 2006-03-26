//$Id$
package org.jboss.seam.example.hibernate;

import static org.jboss.seam.annotations.Outcome.REDISPLAY;

import org.hibernate.Session;
import org.hibernate.validator.Valid;
import org.jboss.seam.annotations.IfInvalid;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.core.FacesMessages;

@Name("changePassword")
@LoggedIn
public class ChangePasswordAction
{

   @In @Out @Valid
   private User user;
   
   @In(create=true)
   private Session bookingDatabase;
   
   private String verify;
   
   @IfInvalid(outcome=REDISPLAY)
   public String changePassword()
   {
      if ( user.getPassword().equals(verify) )
      {
         user = (User) bookingDatabase.merge(user);
         return "main";
      }
      else 
      {
         FacesMessages.instance().add("Re-enter new password");
         bookingDatabase.refresh(user);
         verify=null;
         return null;
      }
   }

   public String cancel()
   {
      bookingDatabase.refresh(user);
      return "main";
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

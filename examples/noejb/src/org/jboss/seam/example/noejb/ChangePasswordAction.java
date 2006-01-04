//$Id$
package org.jboss.seam.example.noejb;

import static org.jboss.seam.annotations.Outcome.REDISPLAY;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.hibernate.Session;
import org.hibernate.validator.Valid;
import org.jboss.seam.annotations.IfInvalid;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;

@Name("changePassword")
@LoggedIn
public class ChangePasswordAction
{

   @In @Out @Valid
   private User user;
   
   @In(create=true)
   private Session bookingDatabase;
   
   @In
   private FacesContext facesContext;
   
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
         facesContext.addMessage(null, new FacesMessage("Re-enter new password"));
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

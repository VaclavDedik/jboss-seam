//$Id$
package org.jboss.seam.example.hibernate;

import static org.jboss.seam.ScopeType.EVENT;
import static org.jboss.seam.annotations.Outcome.REDISPLAY;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.validator.Valid;
import org.jboss.seam.annotations.IfInvalid;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.FacesMessages;

@Scope(EVENT)
@Name("register")
public class RegisterAction
{

   @In @Valid
   private User user;
   
   @In(create=true)
   private Session bookingDatabase;
   
   @In(create=true)
   private FacesMessages facesMessages;
   
   private String verify;
   
   @IfInvalid(outcome=REDISPLAY)
   public String register()
   {
      if ( user.getPassword().equals(verify) )
      {
         List existing = bookingDatabase.createQuery("select username from User where username=:username")
            .setParameter("username", user.getUsername())
            .list();
         if (existing.size()==0)
         {
            bookingDatabase.persist(user);
            return "login";
         }
         else
         {
            facesMessages.add("username already exists");
            return null;
         }
      }
      else 
      {
         facesMessages.add("re-enter your password");
         verify=null;
         return null;
      }
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

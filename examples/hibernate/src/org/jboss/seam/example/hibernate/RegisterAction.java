//$Id$
package org.jboss.seam.example.hibernate;

import static org.jboss.seam.ScopeType.EVENT;

import java.util.List;

import org.hibernate.Session;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.FacesMessages;

@Scope(EVENT)
@Name("register")
public class RegisterAction
{

   @In
   private User user;
   
   @In
   private Session bookingDatabase;
   
   @In
   private FacesMessages facesMessages;
   
   private String verify;
   
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

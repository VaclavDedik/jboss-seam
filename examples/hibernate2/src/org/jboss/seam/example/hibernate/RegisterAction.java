//$Id$
package org.jboss.seam.example.hibernate;

import org.hibernate.Session;
import static org.jboss.seam.ScopeType.EVENT;

import java.util.List;

import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.FacesMessages;

@Scope(EVENT)
@Name("register")
public class RegisterAction {

   @In
   private User user;
   
   @In (create=true)
   private Session bookingDatabase;
   
   @In(create=true)
   private transient FacesMessages facesMessages;
   
   private String verify;
   
   public String register()
   {
      if ( user.getPassword().equals(verify) )
      {
         List existing = bookingDatabase.createQuery("select u.username from User u where u.username=:username")
            .setParameter("username", user.getUsername())
            .list();
         if (existing.size()==0)
         {
            bookingDatabase.persist(user);
            facesMessages.add("Successfully registered as #{user.username}");
            return "login";
         }
         else
         {
            facesMessages.add("Username #{user.username} already exists");
            return null;
         }
      }
      else 
      {
         facesMessages.add("verify", "Re-enter your password");
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

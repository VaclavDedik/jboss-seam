//$Id$
package org.jboss.seam.example.hibernate;

import static org.jboss.seam.ScopeType.EVENT;

import java.util.List;

import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.FacesMessages;

import org.hibernate.Session;

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
   
   private boolean registered;
   
   public void register()
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
            registered = true;
         }
         else
         {
            facesMessages.add("Username #{user.username} already exists");
         }
      }
      else 
      {
         facesMessages.add("verify", "Re-enter your password");
         verify=null;
      }
   }
   
   public void invalid()
   {
      facesMessages.add("Please try again");
   }
   
   public boolean isRegistered()
   {
      return registered;
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

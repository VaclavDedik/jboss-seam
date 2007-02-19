package org.jboss.seam.example.hibernate;

import static org.jboss.seam.ScopeType.EVENT;
import static org.jboss.seam.ScopeType.SESSION;

import java.util.List;

import org.hibernate.Session;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.security.Identity;

@Scope(EVENT)
@Name("authenticator")
public class Authenticator
{
   @In Identity identity;
   
   @In Session bookingDatabase;
   
   @Out(required=false, scope = SESSION)
   private User user;
   
   public boolean authenticate()
   {
      List results = bookingDatabase.createQuery(
            "select u from User u where u.username=:username and u.password=:password")
            .setParameter("username", identity.getUsername())
            .setParameter("password", identity.getPassword())
            .list();
      
      if ( results.size()==0 )
      {
         return false;
      }
      else
      {
         user = (User) results.get(0);
         return true;
      }
   }
}

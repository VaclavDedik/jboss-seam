package org.jboss.seam.example.hibernate;

import static org.jboss.seam.ScopeType.SESSION;

import java.util.List;
import javax.persistence.EntityManager;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.In;

import org.hibernate.Session;

@Name("authenticator")
public class AuthenticatorAction
{
   @In Session bookingDatabase;
   
   @Out(required=false, scope = SESSION)
   private User user;
   
   public boolean authenticate()
   {
      List results = bookingDatabase.createQuery("select u from User u where u.username=#{identity.username} and u.password=#{identity.password}")
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

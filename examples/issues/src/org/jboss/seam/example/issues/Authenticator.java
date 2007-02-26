package org.jboss.seam.example.issues;

import static org.jboss.seam.ScopeType.SESSION;

import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.security.Identity;

@Name("authenticator")
public class Authenticator
{
   @In
   private EntityManager entityManager;   
   
   @In
   private Identity identity;
   
   @Out(required = false, scope = SESSION)
   private User authenticatedUser;

   public boolean authenticate() 
   {
      List results = entityManager.createQuery("select u from User u where u.username=:username and u.password=:password")
         .setParameter("username", identity.getUsername())
         .setParameter("password", identity.getPassword())
         .getResultList();

      if ( results.size()==0 )
      {
         return false;
      }
      else
      {
         authenticatedUser = (User) results.get(0);
         return true;
      }
   }
}

package org.jboss.seam.example.tasks;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.example.tasks.entity.User;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;

/**
 * Application authenticator. User is added to admin role if the admin property
 * is set to true.
 * 
 * @author Jozef Hartinger
 * 
 */
@Name("authenticator")
@Scope(ScopeType.EVENT)
public class Authenticator
{

   @In
   private Identity identity;
   @In
   private Credentials credentials;
   @In
   private EntityManager entityManager;
   @Out(scope = ScopeType.SESSION)
   private User user;

   public boolean authenticate()
   {
      user = entityManager.find(User.class, credentials.getUsername());
      if ((user != null) || (user.getPassword().equals(credentials.getPassword())))
      {
         if (user.isAdmin())
         {
            identity.addRole("admin");
         }
         return true;
      }
      else
      {
         return false;
      }
   }
}

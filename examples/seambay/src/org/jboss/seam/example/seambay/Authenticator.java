package org.jboss.seam.example.seambay;

import static org.jboss.seam.ScopeType.SESSION;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.security.Identity;

@Name("authenticator")
public class Authenticator
{
   @In
   private EntityManager entityManager;
   
   @Out(required = false, scope = SESSION)
   private User authenticatedUser;
   
   @In
   private Identity identity;

   public boolean authenticate() 
   {
      try
      {            
         User user = (User) entityManager.createQuery(
            "from User where username = :username and password = :password")
            .setParameter("username", identity.getUsername())
            .setParameter("password", identity.getPassword())
            .getSingleResult();
         
         authenticatedUser = user;
         
         return true;
      }
      catch (NoResultException ex)
      {
         return false;
      }      
   }
}

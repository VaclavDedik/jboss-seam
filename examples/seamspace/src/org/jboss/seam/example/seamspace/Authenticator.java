package org.jboss.seam.example.seamspace;

import static org.jboss.seam.ScopeType.SESSION;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.security.auth.login.LoginException;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Synchronized;
import org.jboss.seam.core.FacesMessages;

/**
 * Authenticator bean - authenticates the user against the database
 * 
 * @author Shane Bryzak
 */
@Synchronized
@Name("authenticator")
public class Authenticator
{
   @In
   private EntityManager entityManager;
   
   @Out(required = false, scope = SESSION)
   private Member authenticatedMember;

   public void authenticate(String username, String password, Set<String> roles)
      throws LoginException
   {
      try
      {            
         authenticatedMember = (Member) entityManager.createQuery(
            "from Member where username = :username and password = :password")
            .setParameter("username", username)
            .setParameter("password", password)
            .getSingleResult();

         if (authenticatedMember.getRoles() != null)
         {
            for (MemberRole mr : authenticatedMember.getRoles())
               roles.add(mr.getName());
         }
      }
      catch (NoResultException ex)
      {
         throw new LoginException();
      }      
   }   
}

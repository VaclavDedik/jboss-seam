package org.jboss.seam.example.seamspace;

import static org.jboss.seam.ScopeType.SESSION;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

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
   @In(create=true)
   private EntityManager entityManager;
   
   @Out(required = false, scope = SESSION)
   private Member authenticatedMember;

   public boolean authenticate(String username, String password, Set<String> roles) 
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
         
         return true;
      }
      catch (NoResultException ex)
      {
         FacesMessages.instance().add("Invalid username/password");
         return false;
      }      
   }   
}

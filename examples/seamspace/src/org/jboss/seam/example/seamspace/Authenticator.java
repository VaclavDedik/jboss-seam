package org.jboss.seam.example.seamspace;

import static org.jboss.seam.ScopeType.SESSION;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;

/**
 * Authenticator bean - authenticates the user against the database
 * 
 * @author Shane Bryzak
 */
@Name("authenticator")
public class Authenticator
{
   @In
   private EntityManager entityManager;
   
   @Out(required = false, scope = SESSION)
   private Member authenticatedMember;

   public boolean authenticate(String username, String password, Set<String> roles) 
   {
      try
      {            
         Member member = (Member) entityManager.createQuery(
            "from Member where username = :username")
            .setParameter("username", username)
            .getSingleResult();
         
         if (!compareHash(member.getHashedPassword(), password)) {
             return false;
         }
         
         authenticatedMember = member;
         
         if (authenticatedMember.getRoles() != null)
         {
            for (MemberRole mr : authenticatedMember.getRoles())
               roles.add(mr.getName());
         }
         
         return true;
      }
      catch (NoResultException ex)
      {
         return false;
      }      
   }
   
   private boolean compareHash(String hash, String password) {
       if (hash == null || password == null) {
           return false;
       }
       
       String newHash = Hash.instance().hash(password);
       if (newHash == null) {
           return false;
       }

       return hash.equalsIgnoreCase(newHash);
   }

}

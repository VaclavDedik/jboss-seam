package org.jboss.seam.example.jpa;

import static org.jboss.seam.ScopeType.SESSION;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.faces.FacesMessages;

@Name("authenticator")
public class AuthenticatorAction
{
   @In EntityManager em;
   
   @Out(required=false, scope = SESSION)
   private User user;
   
   public boolean authenticate()
   {
      try
      {
         user = (User) em.createQuery("select u from User u where u.username=#{identity.username} and u.password=#{identity.password}")
            .getSingleResult();
         return true;
      }
      catch (NoResultException e) 
      {
         FacesMessages.instance().add("Authentication failed");
         return false;
      }

   }

}

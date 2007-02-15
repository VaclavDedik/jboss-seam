package com.jboss.dvd.seam;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.core.Actor;
import org.jboss.seam.security.Identity;

@Stateless
@Name("authenticator")
public class AuthenticatorAction implements Authenticator
{
   private static final String USER_VAR = "currentUser";

   @PersistenceContext
   private EntityManager em;

   @In Context sessionContext;

   @In Actor actor;

   @In Identity identity;

   public boolean authenticate()
   {
      User found = (User) em.createQuery(
                  "select u from User u where u.userName = :userName and u.password = :password")
            .setParameter("userName", identity.getUsername())
            .setParameter("password", identity.getPassword())
            .getSingleResult();

      sessionContext.set(USER_VAR, found);

      actor.setId(identity.getUsername());

      if (found instanceof Admin)
      {
         actor.getGroupActorIds().add("shippers");
         actor.getGroupActorIds().add("reviewers");
         identity.addRole("admin");
      }
      
      return true;
   }
}

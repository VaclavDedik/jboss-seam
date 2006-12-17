//$Id$
package org.jboss.seam.example.booking;

import java.util.List;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Synchronized;
import org.jboss.seam.core.FacesMessages;

@Stateful
@Scope(ScopeType.SESSION)
@Synchronized
@Name("login")
public class LoginAction implements Login
{
   
   @In(required=false) 
   @Out(required=false)
   private User user;
   
   @PersistenceContext
   private EntityManager em;
   
   @In
   private FacesMessages facesMessages;
   
   private boolean loggedIn;

   public void login()
   {
      List<User> results = em.createQuery("select u from User u where u.username=:username and u.password=:password")
            .setParameter("username", user.getUsername())
            .setParameter("password", user.getPassword())
            .getResultList();
      
      if ( results.size()==0 )
      {
         facesMessages.add("Invalid login");
      }
      else
      {
         user = results.get(0);
         loggedIn = true;
         facesMessages.add("Welcome, #{user.name}");
      }
      
   }

   public void logout()
   {
      loggedIn = false;
      Seam.invalidateSession();
   }

   public void validateLogin()
   {
      if ( !loggedIn )
      {
         facesMessages.add("Please log in first");
      }
   }

   public boolean isLoggedIn()
   {
      return loggedIn;
   }
   
   @Destroy @Remove
   public void destroy() {}
   
}

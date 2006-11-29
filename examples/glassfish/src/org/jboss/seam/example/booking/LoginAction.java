//$Id$
package org.jboss.seam.example.booking;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.FacesMessages;

@Stateless
@Name("login")
public class LoginAction implements Login
{
   
   @In @Out
   private User user;
   
   @PersistenceContext
   private EntityManager em;

   public String login()
   {
      List<User> results = em.createQuery("select u from User u where u.username=:username and u.password=:password")
            .setParameter("username", user.getUsername())
            .setParameter("password", user.getPassword())
            .getResultList();
      
      if ( results.size()==0 )
      {
         FacesMessages.instance().add("Invalid login");
         return "login";
      }
      else
      {
         user = results.get(0);
         Contexts.getSessionContext().set("loggedIn", true);
         FacesMessages.instance().add("Welcome, #{user.name}");
         return "main";
      }
      
   }

}

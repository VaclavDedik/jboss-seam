//$Id$
package org.jboss.seam.example.hibernate;

import java.util.List;
import org.hibernate.Session;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.FacesMessages;

@Name("login")
public class LoginAction {
   
   @In @Out
   private User user;
   
   @In (create=true)
   private Session bookingDatabase;

   public String login()
   {
      List<User> results = bookingDatabase.createQuery("select u from User u where u.username=:username and u.password=:password")
            .setParameter("username", user.getUsername())
            .setParameter("password", user.getPassword())
            .list();
      
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

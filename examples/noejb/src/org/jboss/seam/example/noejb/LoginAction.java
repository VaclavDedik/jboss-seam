//$Id$
package org.jboss.seam.example.noejb;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.hibernate.Session;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.contexts.Contexts;

@Name("login")
public class LoginAction
{
   
   @In @Out
   private User user;
   
   @In(create=true)
   private Session bookingDatabase;

   public String login()
   {
      List<User> results = bookingDatabase.createQuery("from User where username=:username and password=:password")
            .setParameter("username", user.getUsername())
            .setParameter("password", user.getPassword())
            .list();
      
      if ( results.size()==0 )
      {
         FacesContext.getCurrentInstance()
               .addMessage(null, new FacesMessage("Invalid login"));
         return "login";
      }
      else
      {
         user = results.get(0);
         Contexts.getSessionContext().set("loggedIn", true);         
         return "main";
      }
      
   }

}

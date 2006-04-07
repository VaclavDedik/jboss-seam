//$Id$
package org.jboss.seam.example.hibernate;

import static org.jboss.seam.ScopeType.STATELESS;

import java.util.List;

import org.hibernate.Session;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.core.FacesMessages;

@Name("login")
@Scope(STATELESS)
public class LoginAction
{
   
   @In @Out
   private User user;
   
   @In(create=true)
   private Session bookingDatabase;
   
   @In
   private Context sessionContext;
   
   @In(create=true)
   private FacesMessages facesMessages;

   public String login()
   {
      List<User> results = bookingDatabase.createQuery("from User where username=:username and password=:password")
            .setParameter("username", user.getUsername())
            .setParameter("password", user.getPassword())
            .list();
      
      if ( results.size()==0 )
      {
         facesMessages.add("Invalid login");
         return "login";
      }
      else
      {
         user = results.get(0);
         sessionContext.set("loggedIn", true);         
         return "main";
      }
      
   }

}

package actions;

import java.io.IOException;
import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.FacesMessages;

import domain.Blog;

@Name("loginAction")
@Scope(ScopeType.SESSION)
public class LoginAction implements Serializable
{
   @In(create=true) Blog blog;
      
   @In FacesMessages facesMessages;
   
   String password;
   boolean loggedIn;
   
   public void login() throws IOException
   {
      if ( blog.getPassword().equals(password) )
      {
         loggedIn = true;
      }
      else
      {
         facesMessages.add("incorrect password");
      }
   }
   
   public String challenge()
   {
      return loggedIn ? null : "/login.xhtml";
   }

   public String getPassword()
   {
      return password;
   }

   public void setPassword(String password)
   {
      this.password = password;
   }

   public boolean isLoggedIn()
   {
      return loggedIn;
   }
      
}

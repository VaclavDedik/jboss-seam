package actions;

import java.io.IOException;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.FacesMessages;

import domain.Blog;

@Name("loginAction")
@Scope(ScopeType.SESSION)
public class LoginAction
{
   @In(create=true) private Blog blog;
   
   @NotNull @Length(min=5, max=50)
   private String password;
   
   @In(create=true) FacesMessages facesMessages;
   
   private boolean loggedIn;
   
   public String login() throws IOException
   {
      if ( blog.getPassword().equals(password) )
      {
         loggedIn = true;
         return "/post.xhtml";
      }
      else
      {
         facesMessages.add("incorrect password");
         return null;
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

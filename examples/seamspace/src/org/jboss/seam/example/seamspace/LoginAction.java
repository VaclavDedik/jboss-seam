package org.jboss.seam.example.seamspace;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Synchronized;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.security.SeamSecurityManager;
import org.jboss.seam.security.UsernamePasswordToken;

/**
 * Login action
 * 
 * @author Shane Bryzak
 */
@Stateful
@Scope(ScopeType.SESSION)
@Synchronized
@Name("login")
public class LoginAction implements LoginLocal
{
   @In(required = false)
   @Out(required = false)
   Member member;

   private boolean loggedIn;

   public void login()
   {
      try
      {
         LoginContext lc = SeamSecurityManager.instance().createLoginContext();
         lc.getSubject().getPrincipals().add(new UsernamePasswordToken(
               member.getUsername(), member.getPassword()));
         lc.login();
         
         loggedIn = true;
      }
      catch (LoginException ex)
      {
         FacesMessages.instance().add("Invalid login");
      }
   }

   public void logout() 
   {
      loggedIn = false;
      Seam.invalidateSession();
   }

   public boolean isLoggedIn()
   {
      return loggedIn;
   }

   @Remove
   @Destroy
   public void destroy() { }
}

package org.jboss.seam.example.seamspace;

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
import org.jboss.seam.security.AuthenticationException;
import org.jboss.seam.security.authenticator.Authenticator;

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
  @In(required = false) @Out(required = false) Member member;
  
  private boolean loggedIn;

  public void login()
  {
    try
    {
      Authenticator.instance().authenticate(member.getUsername(), member.getPassword());
      loggedIn = true;      
    }
    catch (AuthenticationException ex)
    {
      FacesMessages.instance().add("Invalid login");
    }
  }

  public void logout()
  {
	loggedIn = false;
	Authenticator.instance().unauthenticateSession();
    Seam.invalidateSession();
  }
  
  public boolean isLoggedIn()
  {
	return loggedIn;
  }
  
  @Remove @Destroy
  public void destroy() { }
}

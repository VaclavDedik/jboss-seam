package org.jboss.seam.example.security;

import javax.ejb.Stateless;

import static org.jboss.seam.ScopeType.SESSION;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.security.Authentication;
import org.jboss.seam.security.AuthenticationException;
import org.jboss.seam.security.UsernamePasswordToken;
import org.jboss.seam.security.authenticator.Authenticator;

/**
 * Authenticates the user.
 *
 * @author Shane Bryzak
 */
@Stateless
@Name("login")
public class LoginAction implements LoginLocal
{
  @In(value = "org.jboss.seam.security.Authenticator") Authenticator authenticator;
  @Out(value = "org.jboss.seam.security.Authentication", scope = SESSION, required = false) Authentication authentication;

  @In(required = false) @Out(required = false) User user;

  public String login()
  {
    authentication = new UsernamePasswordToken(user.getUsername(), user.getPassword());
    try
    {
      authenticator.authenticate(authentication);
      return "success";
    }
    catch (AuthenticationException ex)
    {
      FacesMessages.instance().add("Invalid login");
      return "login";
    }
  }

  public String logout()
  {
    authentication = null;
    Contexts.getSessionContext().remove("org.jboss.seam.security.Authentication");
    return "login";
  }
}

package org.jboss.seam.example.security;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.security.AuthenticationException;
import org.jboss.seam.security.authenticator.Authenticator;
import org.jboss.seam.Seam;
import javax.faces.application.FacesMessage;

/**
 * Authenticates the user.
 *
 * @author Shane Bryzak
 */
@Stateless
@Name("login")
public class LoginAction implements LoginLocal
{
  @In(required = false) @Out(required = false) User user;

  public String login()
  {
    try
    {
      Authenticator.instance().authenticate(user.getUsername(), user.getPassword());
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
    Authenticator.instance().unauthenticateSession();
    Seam.invalidateSession();
    return "login";
  }
}

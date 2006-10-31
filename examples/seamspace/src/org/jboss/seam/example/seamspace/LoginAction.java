package org.jboss.seam.example.seamspace;

import javax.ejb.Stateless;

import org.jboss.seam.Seam;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.security.AuthenticationException;
import org.jboss.seam.security.authenticator.Authenticator;

/**
 * Login action
 *
 * @author Shane Bryzak
 */
@Stateless
@Name("loginAction")
public class LoginAction implements LoginLocal
{
  @In(required = false) @Out(required = false) Member member;

  public String login()
  {
    try
    {
      Authenticator.instance().authenticate(member.getUsername(), member.getPassword());
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

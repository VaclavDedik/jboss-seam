package org.jboss.seam.example.security;

import javax.ejb.Stateless;

import static org.jboss.seam.ScopeType.SESSION;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.security.Authentication;
import org.jboss.seam.security.AuthenticationException;
import org.jboss.seam.security.UsernamePasswordToken;
import org.jboss.seam.security.authenticator.Authenticator;

/**
 * Authenticates the user against the Realm.
 *
 * @author Shane Bryzak
 */
@Stateless
@Name("login")
public class LoginAction implements LoginLocal
{
  @In(value = "org.jboss.seam.security.Authenticator") Authenticator authenticator;
  @Out(scope = SESSION) Authentication authentication;

  @In @Out User user;

  public String login()
  {
    System.out.println("login() called");

    authentication = new UsernamePasswordToken(user.getUsername(), user.getPassword());
    try
    {
      authenticator.authenticate(authentication);
      return "success";
    }
    catch (AuthenticationException ex)
    {
      return "login";
    }
  }
}

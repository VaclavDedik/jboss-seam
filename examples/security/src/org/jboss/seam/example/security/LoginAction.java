package org.jboss.seam.example.security;

import org.jboss.seam.annotations.Name;
import javax.ejb.Stateless;
import org.jboss.seam.annotations.In;
import org.jboss.seam.security.realm.Realm;
import java.security.Principal;

/**
 * Authenticates the user against the Realm.
 *
 * @author Shane Bryzak
 */
@Stateless
@Name("login")
public class LoginAction implements LoginLocal
{
  @In("org.jboss.seam.security.realm.Realm") Realm realm;

  @In User user;

  public String login()
  {
    System.out.println("login() called");

    try
    {
      Principal principal = realm.authenticate(user.getUsername(), user.getPassword());
      System.out.println("Got principal: " + principal);
      return "success";
    }
    catch (Exception ex)
    {
      return "login";
    }
  }
}

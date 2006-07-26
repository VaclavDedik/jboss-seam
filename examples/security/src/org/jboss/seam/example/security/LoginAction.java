package org.jboss.seam.example.security;

import java.security.Principal;
import javax.ejb.Stateless;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.security.realm.Realm;

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

  @In @Out User user;

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
      FacesMessages.instance().add("Invalid login, please check your username and password are correct");
      return "login";
    }
  }
}

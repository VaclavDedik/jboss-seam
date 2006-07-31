package org.jboss.seam.security;

/**
 * <p> </p>
 *
 * @author Shane Bryzak
 */
public class UsernamePasswordToken implements Authentication
{
  private String[] roles;
  private Object credentials;
  private Object principal;
  private boolean authenticated = false;

  public UsernamePasswordToken(String username, String password)
  {
    this.principal = username;
    this.credentials = password;
  }

  public String getName()
  {
    return principal.toString();
  }

  public String[] getRoles()
  {
    return roles;
  }

  public Object getCredentials()
  {
    return credentials;
  }

  public Object getPrincipal()
  {
    return principal;
  }

  public boolean isAuthenticated()
  {
    return authenticated;
  }
}

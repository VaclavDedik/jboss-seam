package org.jboss.seam.security;

import java.io.Serializable;
import java.security.Principal;

import static org.jboss.seam.ScopeType.SESSION;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;

/**
 * An Authentication represents either a login token or an authenticated Principal.
 *
 * @author Shane Bryzak
 */
@Name("org.jboss.seam.security.authentication")
@Scope(SESSION)
public abstract class Authentication implements Principal, Serializable
{
  protected boolean authenticated;
  protected boolean valid;

  public static Authentication instance()
  {
    if (!Contexts.isSessionContextActive())
       throw new IllegalStateException("No active session context");

    Authentication instance = (Authentication) Component.getInstance(
        Authentication.class, ScopeType.SESSION, true);

    if (instance==null)
    {
      throw new AuthenticationException(
          "No Authentication could be created, make sure the Component exists in session scope");
    }

    return instance;
  }

  public abstract String[] getRoles();
  public abstract Object getCredentials();
  public abstract Object getPrincipal();

  public final boolean isAuthenticated()
  {
    return authenticated;
  }

  public final boolean isValid()
  {
    return valid;
  }

  public final void invalidate()
  {
    valid = false;
  }

  /**
   * Checks if the authenticated user contains the specified role.
   *
   * @param role String
   * @return boolean Returns true if the authenticated user contains the role,
   * or false if otherwise.
   */
  public boolean isUserInRole(String role)
  {
    for (String r : getRoles())
    {
      if (r.equals(role))
        return true;
    }
    return false;
  }
}

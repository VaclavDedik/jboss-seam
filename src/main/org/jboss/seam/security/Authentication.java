package org.jboss.seam.security;

import java.io.Serializable;
import java.security.Principal;
import java.security.acl.Acl;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import static org.jboss.seam.ScopeType.SESSION;

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
      throw new IllegalStateException(
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
}

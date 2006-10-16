package org.jboss.seam.security;

import static org.jboss.seam.ScopeType.APPLICATION;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;

/**
 * Holds configuration settings and provides functionality for the security API
 *
 * @author Shane Bryzak
 */
@Scope(APPLICATION)
@Name("org.jboss.seam.securityManager")
public class SeamSecurityManager
{
  /**
   * Directs the user to a login page.
   */
  private String loginAction = "login";

  /**
   * Directs the user to a security error page.
   */
  private String securityErrorAction = "securityError";

  public static SeamSecurityManager instance()
  {
    if (!Contexts.isApplicationContextActive())
       throw new IllegalStateException("No active application context");

    SeamSecurityManager instance = (SeamSecurityManager) Component.getInstance(
        SeamSecurityManager.class, ScopeType.APPLICATION, true);

    if (instance==null)
    {
      throw new IllegalStateException(
          "No SeamSecurityManager could be created, make sure the Component exists in application scope");
    }

    return instance;
  }

  public String getLoginAction()
  {
    return loginAction;
  }

  public void setLoginAction(String loginAction)
  {
    this.loginAction = loginAction;
  }

  public String getSecurityErrorAction()
  {
    return securityErrorAction;
  }

  public void setSecurityErrorAction(String securityErrorAction)
  {
    this.securityErrorAction = securityErrorAction;
  }
}

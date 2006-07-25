package org.jboss.seam.security.authenticator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.security.config.SecurityConfig;

/**
 * Abstract base class for Authenticator implementations.
 *
 * @author Shane Bryzak
 */
public abstract class BaseAuthenticator implements Authenticator
{
  protected SecurityConfig securityConfig;

  public void setSecurityConfig(SecurityConfig securityConfig)
  {
    this.securityConfig = securityConfig;
  }

  public boolean processLogin(HttpServletRequest request, HttpServletResponse response)
  {
    return false;
  }
}

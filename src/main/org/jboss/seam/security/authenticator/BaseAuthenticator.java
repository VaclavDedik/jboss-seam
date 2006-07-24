package org.jboss.seam.security.authenticator;

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
}

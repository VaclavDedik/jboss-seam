package org.jboss.seam.security.authenticator;

import org.jboss.seam.security.config.SecurityConfig;

/**
 * The authenticator interface
 *
 * @author Shane Bryzak
 */
public interface Authenticator
{
  void setSecurityConfig(SecurityConfig securityConfig);
}

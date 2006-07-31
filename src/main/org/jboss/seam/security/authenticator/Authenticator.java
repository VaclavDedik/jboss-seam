package org.jboss.seam.security.authenticator;

import org.jboss.seam.security.Authentication;
import org.jboss.seam.security.AuthenticationException;

/**
 * Authenticates an Authentication.
 *
 * @author Shane Bryzak
 */
public interface Authenticator
{
  Authentication authenticate(Authentication authentication)
      throws AuthenticationException;
}

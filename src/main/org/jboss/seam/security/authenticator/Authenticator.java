package org.jboss.seam.security.authenticator;

import org.jboss.seam.security.AuthenticationException;
import org.jboss.seam.security.Authentication;

/**
 *
 *
 * @author Shane Bryzak
 */
public interface Authenticator
{
  Authentication authenticate(Authentication authentication)
      throws AuthenticationException;
  void unauthenticate(Authentication authentication);
}

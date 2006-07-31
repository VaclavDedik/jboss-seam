package org.jboss.seam.security.authenticator;

import org.jboss.seam.security.Authentication;
import org.jboss.seam.security.AuthenticationException;

/**
 * <p> </p>
 *
 * @author Shane Bryzak
 */
public class ProviderAuthenticator implements Authenticator
{
  public Authentication authenticate(Authentication authentication)
      throws AuthenticationException
  {

    return authentication;
  }
}

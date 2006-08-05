package org.jboss.seam.security.provider;

import org.jboss.seam.security.Authentication;
import org.jboss.seam.security.AuthenticationException;

/**
 *
 *
 * @author Shane Bryzak
 */
public interface AuthenticationProvider
{
  Authentication authenticate(Authentication authentication)
        throws AuthenticationException;
}

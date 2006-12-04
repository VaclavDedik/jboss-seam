package org.jboss.seam.security.provider;

import org.jboss.seam.security.Identity;
import org.jboss.seam.security.AuthenticationException;

/**
 * @author Shane Bryzak
 */
public interface AuthenticationProvider
{
  Identity authenticate(Identity authentication)
        throws AuthenticationException;
}

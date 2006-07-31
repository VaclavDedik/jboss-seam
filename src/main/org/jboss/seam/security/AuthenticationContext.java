package org.jboss.seam.security;

import java.io.Serializable;

/**
 *
 * @author Shane Bryzak
 */
public interface AuthenticationContext extends Serializable
{
  Authentication getAuthentication();
  void setAuthentication(Authentication authentication);
}

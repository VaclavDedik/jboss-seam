package org.jboss.seam.security;

import java.io.Serializable;
import java.security.Principal;

/**
 * <p> </p>
 *
 * @author Shane Bryzak
 */
public interface Authentication extends Principal, Serializable
{
  String[] getRoles();
  Object getCredentials();
  Object getPrincipal();
  boolean isAuthenticated();
}

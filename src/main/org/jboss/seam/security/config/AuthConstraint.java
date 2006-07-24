package org.jboss.seam.security.config;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents the <auth-constraint> settings in the config file.
 *
 * @author Shane Bryzak
 */
public class AuthConstraint
{
  private Set<String> roles = new HashSet<String>();

  public Set<String> getRoles()
  {
    return roles;
  }

  public void setRoles(Set<String> roles)
  {
    this.roles = roles;
  }
}

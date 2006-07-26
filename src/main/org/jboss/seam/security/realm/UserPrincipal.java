package org.jboss.seam.security.realm;

import java.security.Principal;

/**
 * A user principal.
 *
 * @author Shane Bryzak
 */
public class UserPrincipal implements Principal
{
  private String name;

  public UserPrincipal(String name)
  {
    this.name = name;
  }

  public String getName()
  {
    return name;
  }
}

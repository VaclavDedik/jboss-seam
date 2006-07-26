package org.jboss.seam.security.realm;

import java.security.Principal;

/**
 * A role principal
 *
 * @author Shane Bryzak
 */
public class RolePrincipal implements Principal
{
  private String name;

  public RolePrincipal(String name)
  {
    this.name = name;
  }

  public String getName()
  {
    return name;
  }
}

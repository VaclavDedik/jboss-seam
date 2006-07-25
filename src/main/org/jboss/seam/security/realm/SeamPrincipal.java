package org.jboss.seam.security.realm;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A simple Principal implementation
 *
 * @author Shane Bryzak
 */
public class SeamPrincipal implements Principal
{
  private String name = null;
  private Principal userPrincipal = null;

  private Realm realm = null;
  private Set<String> roles;

  public SeamPrincipal(Realm realm, String name, List roles, Principal userPrincipal)
  {
    this.realm = realm;
    this.name = name;
    this.userPrincipal = userPrincipal;

    if (roles != null)
      this.roles = new HashSet<String>(roles);
  }

  public String getName()
  {
    return name;
  }

  public Realm getRealm()
  {
    return realm;
  }

  void setRealm(Realm realm)
  {
    this.realm = realm;
  }

  public Set<String> getRoles()
  {
    return roles;
  }

  public Principal getUserPrincipal()
  {
    if (userPrincipal != null)
    {
      return userPrincipal;
    }
    else
    {
      return this;
    }
  }

  public boolean hasRole(String role)
  {
    if (role == null)
      return false;
    else if ("*".equals(role))
      return true;

    return (roles.contains(role));
  }
}

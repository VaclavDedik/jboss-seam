package org.jboss.seam.security.acl;

import java.security.Principal;
import java.security.acl.AclEntry;
import java.security.acl.Permission;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Default AclEntry implementation.
 *
 * @author Shane Bryzak
 */
public class AclEntryImpl implements AclEntry
{
  private Set<Permission> permissions = new HashSet<Permission>();
  private boolean negative;
  private Principal principal;

  public boolean addPermission(Permission permission)
  {
    return permissions.add(permission);
  }

  public boolean removePermission(Permission permission)
  {
    return permissions.remove(permission);
  }

  public boolean checkPermission(Permission permission)
  {
    return permissions.contains(permission);
  }

  public Enumeration permissions()
  {
    final Iterator iter = permissions.iterator();

    return new Enumeration() {
      public boolean hasMoreElements() {
        return iter.hasNext();
      }
      public Object nextElement() {
        return iter.next();
      }
    };
  }

  public void setNegativePermissions()
  {
    negative = true;
  }

  public boolean isNegative()
  {
    return negative;
  }

  public Principal getPrincipal()
  {
    return principal;
  }

  public boolean setPrincipal(Principal user)
  {
    if (principal != null)
      return false;

    principal = user;
    return true;
  }

  public String toString()
  {
    final String hdr = "AclEntry[";
    StringBuilder sb = new StringBuilder(hdr);

    for (Permission p : permissions)
    {
      if (sb.length() > hdr.length())
        sb.append(',');
      sb.append(p.toString());
    }
    sb.append(']');
    return sb.toString();
  }

  public Object clone()
  {
    AclEntryImpl clone = new AclEntryImpl();
    for (Permission p : permissions)
    {
      clone.addPermission(p);
    }
    clone.negative = negative;

    return clone;
  }
}

package org.jboss.seam.security;

import java.io.Serializable;
import java.security.Principal;
import java.security.acl.Group;
import java.security.acl.Permission;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * A Role implementation.  Roles can contain other roles.
 *
 * @author Shane Bryzak
 */
public class Role implements Group, Serializable
{
  /**
   * The name of the role
   */
  private String name;

  /**
   * The members of this role.  This role has the authority to perform any action
   * that any of its members (or member's members, ad infinitum) can perform.
   */
  private Set<Principal> members = new HashSet<Principal>();

  /**
   * A set of permissions explicitly assigned to this role.
   */
  private Set<Permission> permissions = new HashSet<Permission>();

  public Role(String name)
  {
    this.name = name;
  }

  public boolean addPermission(Permission permission)
  {
    return permissions.add(permission);
  }

  public boolean hasPermission(Permission permission)
  {
    return permissions.contains(permission);
  }

  public boolean removePermission(Permission permission)
  {
    return permissions.remove(permission);
  }

  public boolean addMember(Principal user)
  {
    return members.add(user);
  }

  public boolean isMember(Principal member)
  {
    if (members.contains(member))
      return true;
    else
    {
      for (Principal m : members)
      {
        if (m instanceof Group && ((Group) m).isMember(member))
          return true;
      }
    }
    return false;
  }

  public Enumeration<? extends Principal> members()
  {
    return Collections.enumeration(members);
  }

  public boolean removeMember(Principal user)
  {
    return members.remove(user);
  }

  public String getName()
  {
    return name;
  }

  public boolean equals(Object obj)
  {
    if (!(obj instanceof Role))
      return false;

    Role other = (Role) obj;

    return other.name.equals(name);
  }

  public int hashCode()
  {
    return name.hashCode();
  }
}

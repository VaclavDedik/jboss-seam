package org.jboss.seam.security;

import java.security.acl.Permission;

/**
 * Represents permissions for a Seam component.
 *
 * @author Shane Bryzak
 */
public class SeamPermission implements Permission
{
  private String name;
  private String action;

  /**
   *
   * @param name String
   * @param actions String
   */
  public SeamPermission(String name, String action)
  {
    if (name == null || "".equals(name.trim()))
      throw new IllegalArgumentException("Permission name is required");

    this.name = name;
    this.action = action;
  }

  public String getName()
  {
    return name;
  }

  public String getAction()
  {
    return action;
  }

  public String toString()
  {
    return String.format("[name=%s,action=%s]", name, action);
  }

  public boolean equals(Object obj)
  {
    if (!(obj instanceof SeamPermission))
      return false;

    SeamPermission other = (SeamPermission) obj;

    return other.name.equals(name) && other.action.equals(this.action);
  }

  public int hashCode()
  {
    return (name.hashCode() * 11) ^ (action.hashCode() * 13);
  }
}

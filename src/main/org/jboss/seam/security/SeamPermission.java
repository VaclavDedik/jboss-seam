package org.jboss.seam.security;

import java.security.Permission;
import java.util.Arrays;

/**
 * Represents permissions for a Seam component.
 *
 * @author Shane Bryzak
 */
public class SeamPermission extends Permission
{
  private String actions;

  /**
   *
   * @param name String
   * @param actions String
   */
  public SeamPermission(String name, String actions)
  {
    super(name);

    String[] parts = actions.split(",");
    Arrays.sort(parts);

    StringBuilder sorted = new StringBuilder();
    for (String action : parts)
    {
      if (sorted.length() > 0)
        sorted.append(',');
      sorted.append(action);
    }

    this.actions = sorted.toString();
  }

  public boolean implies(Permission permission)
  {
    return false;
  }

  public String getActions()
  {
    return actions;
  }

  public boolean equals(Object obj)
  {
    if (!(obj instanceof SeamPermission))
      return false;

    SeamPermission other = (SeamPermission) obj;

    return other.getName().equals(getName()) && other.actions.equals(this.actions);
  }

  public int hashCode()
  {
    return (getName().hashCode() * 11) ^ (actions.hashCode() * 13);
  }
}

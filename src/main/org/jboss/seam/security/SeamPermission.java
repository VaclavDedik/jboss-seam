package org.jboss.seam.security;

import java.security.acl.Permission;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents permissions for a Seam component.
 *
 * @author Shane Bryzak
 */
public class SeamPermission implements Permission
{
  private String name;
  private String actions;

  private Set<String> actionSet = new HashSet<String>();
  /**
   *
   * @param name String
   * @param actions String
   */
  public SeamPermission(String name, String actions)
  {
    if (name == null || "".equals(name.trim()))
      throw new IllegalArgumentException("Permission name is required");

    this.name = name;

    String[] parts = actions.split(",");
    Arrays.sort(parts);

    StringBuilder sorted = new StringBuilder();
    for (String action : parts)
    {
      actionSet.add(action);

      if (sorted.length() > 0)
        sorted.append(',');
      sorted.append(action);
    }

    this.actions = sorted.toString();
  }

  public String getName()
  {
    return name;
  }

  public String getActions()
  {
    return actions;
  }

  /**
   * Returns true if this permission contains the specified action.
   *
   * @param action String
   * @return boolean
   */
  public boolean containsAction(String action)
  {
    return actionSet.contains(action);
  }

  public boolean equals(Object obj)
  {
    if (!(obj instanceof SeamPermission))
      return false;

    SeamPermission other = (SeamPermission) obj;

    return other.name.equals(name) && other.actions.equals(this.actions);
  }

  public int hashCode()
  {
    return (name.hashCode() * 11) ^ (actions.hashCode() * 13);
  }
}

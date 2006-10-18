package org.jboss.seam.security;

import java.security.Permission;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;

/**
 * Represents permissions for a Seam component.
 *
 * @author Shane Bryzak
 */
public class SeamPermission extends Permission
{
  private String actions;

  private Set<String> actionSet = new HashSet<String>();
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
      actionSet.add(action);

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

    return other.getName().equals(getName()) && other.actions.equals(this.actions);
  }

  public int hashCode()
  {
    return (getName().hashCode() * 11) ^ (actions.hashCode() * 13);
  }
}

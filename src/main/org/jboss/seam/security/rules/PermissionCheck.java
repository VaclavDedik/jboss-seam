package org.jboss.seam.security.rules;

/**
 * Used to assert permission requirements into a WorkingMemory when evaluating
 * a @Restrict expression.  The consequence of the rule is responsible for
 * granting the permission.
 *
 * @author Shane Bryzak
 */
public class PermissionCheck
{
  private String name;
  private String action;
  private boolean granted;

  public PermissionCheck(String name, String action)
  {
    this.name = name;
    this.action = action;
    this.granted = false;
  }

  public String getName()
  {
    return name;
  }

  public String getAction()
  {
    return action;
  }

  public void grant()
  {
    this.granted = true;
  }

  public boolean isGranted()
  {
    return granted;
  }
}

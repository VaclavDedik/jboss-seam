package org.jboss.seam.example.seamspace;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.jboss.seam.annotations.Name;

/**
 * Represents a security role.
 *
 * @author Shane Bryzak
 */
@Entity
@Name("role")
public class Role implements Serializable
{
  private Integer roleId;
  private String name;

  @Id
  public Integer getRoleId()
  {
    return roleId;
  }

  public void setRoleId(Integer roleId)
  {
    this.roleId = roleId;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }
}

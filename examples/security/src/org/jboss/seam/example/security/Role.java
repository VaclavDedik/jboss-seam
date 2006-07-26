package org.jboss.seam.example.security;

import javax.persistence.Entity;
import org.jboss.seam.annotations.Name;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;

/**
 * A user role.
 *
 * @author Shane Bryzak
 * @version 1.0
 */
@Entity
@Name("userrole")
public class Role
{
  private Integer roleId;
  private User user;
  private String role;

  @Id
  public Integer getRoleId()
  {
    return roleId;
  }

  @ManyToOne
  public User getUser()
  {
    return user;
  }

  public String getRole()
  {
    return role;
  }

  public void setRoleId(Integer roleId)
  {
    this.roleId = roleId;
  }

  public void setUser(User user)
  {
    this.user = user;
  }

  public void setRole(String role)
  {
    this.role = role;
  }
}

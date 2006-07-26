package org.jboss.seam.example.security;

import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.jboss.seam.annotations.Name;
import javax.persistence.JoinColumn;

/**
 * <p>PROPRIETARY/CONFIDENTIAL Use of this product is subject to license terms.
 * Copyright (c) 2006 Symantec Corporation. All rights reserved.</p>
 *
 * @author Shane Bryzak
 * @version 1.0
 */
@Entity
@Name("user")
public class User
{
  private String username;
  private String password;
  private Set<Role> roles;

  @Id
  public String getUsername()
  {
    return username;
  }

  public String getPassword()
  {
    return password;
  }

  @OneToMany
  @JoinColumn(name = "USERNAME")
  public Set<Role> getRoles()
  {
    return roles;
  }

  public void setPassword(String password)
  {
    this.password = password;
  }

  public void setUsername(String username)
  {
    this.username = username;
  }

  public void setRoles(Set<Role> roles)
  {
    this.roles = roles;
  }
}

package org.jboss.seam.example.security;

import javax.persistence.Entity;
import org.jboss.seam.annotations.Name;
import javax.persistence.Id;

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
  private Integer userId;
  private String username;
  private String password;

  public String getPassword()
  {
    return password;
  }

  public void setPassword(String password)
  {
    this.password = password;
  }

  public void setUsername(String username)
  {
    this.username = username;
  }

  @Id
  public void setUserId(Integer userId)
  {
    this.userId = userId;
  }

  public Integer getUserId()
  {
    return userId;
  }

  public String getUsername()
  {
    return username;
  }
}

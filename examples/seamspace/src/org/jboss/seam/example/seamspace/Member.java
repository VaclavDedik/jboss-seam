package org.jboss.seam.example.seamspace;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.jboss.seam.annotations.Name;

/**
 * A user account
 *
 * @author Shane Bryzak
 */
@Entity
@Name("member")
public class Member implements Serializable
{
  private Integer memberId;
  private String username;
  private String password;

  @Id
  public Integer getMemberId()
  {
    return memberId;
  }

  public void setMemberId(Integer memberId)
  {
    this.memberId = memberId;
  }

  public String getUsername()
  {
    return username;
  }

  public void setUsername(String username)
  {
    this.username = username;
  }

  public String getPassword()
  {
    return password;
  }

  public void setPassword(String password)
  {
    this.password = password;
  }
}

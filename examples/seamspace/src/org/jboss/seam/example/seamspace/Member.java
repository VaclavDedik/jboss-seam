package org.jboss.seam.example.seamspace;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import org.jboss.seam.annotations.Name;

/**
 * A member account
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

  private Set<MemberRole> roles;

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

  @ManyToMany
  @JoinTable(name = "MemberRoles",
             joinColumns=@JoinColumn(name="MEMBER_ID"), 
		     inverseJoinColumns=@JoinColumn(name="ROLE_ID"))    
  public Set<MemberRole> getRoles()
  {
    return roles;
  }

  public void setRoles(Set<MemberRole> roles)
  {
    this.roles = roles;
  }
}

package org.jboss.seam.example.seamspace;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.jboss.seam.annotations.Name;

/**
 * A member account
 * 
 * @author Shane Bryzak
 */
@Entity
@Name("member")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class Member implements Serializable
{
   private static final long serialVersionUID = 5179242727836683375L;
   
   private Integer memberId;
   private String username;
   private String password;
   private String name;
   private MemberImage picture;
   private Set<MemberRole> roles;
   private Set<MemberImage> images;

   @Id
   public Integer getMemberId()
   {
      return memberId;
   }

   public void setMemberId(Integer memberId)
   {
      this.memberId = memberId;
   }

   @NotNull
   @Length(min = 6, max = 20)
   public String getUsername()
   {
      return username;
   }

   public void setUsername(String username)
   {
      this.username = username;
   }

   @NotNull
   @Length(min = 6, max = 20)
   public String getPassword()
   {
      return password;
   }

   public void setPassword(String password)
   {
      this.password = password;
   }

   @NotNull
   @Length(min = 3, max = 40)
   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   @ManyToMany
   @JoinTable(name = "MemberRoles", joinColumns = @JoinColumn(name = "MEMBER_ID"), inverseJoinColumns = @JoinColumn(name = "ROLE_ID"))
   public Set<MemberRole> getRoles()
   {
      return roles;
   }

   public void setRoles(Set<MemberRole> roles)
   {
      this.roles = roles;
   }

   @OneToOne(fetch = FetchType.LAZY)
   public MemberImage getPicture()
   {
      return picture;
   }

   public void setPicture(MemberImage picture)
   {
      this.picture = picture;
   }

   @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
   public Set<MemberImage> getImages()
   {
      return images;
   }

   public void setImages(Set<MemberImage> images)
   {
      this.images = images;
   }
}

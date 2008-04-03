package org.jboss.seam.example.seamspace;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.validator.NotNull;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "username"))
public class MemberAccount implements Serializable
{
   private static final long serialVersionUID = 6368734442192368866L;
   
   private Integer accountId;
   private String username;
   private String passwordHash;
   private boolean enabled;   
   private Set<MemberAccount> memberships;
   private Member member;   
   
   @Id @GeneratedValue
   public Integer getAccountId()
   {
      return accountId;
   }
   
   public void setAccountId(Integer accountId)
   {
      this.accountId = accountId;
   }
   
   @NotNull
   public String getUsername()
   {
      return username;
   }
   
   public void setUsername(String username)
   {
      this.username = username;
   }
   
  
   public String getPasswordHash()
   {
      return passwordHash;
   }
   
   public void setPasswordHash(String passwordHash)
   {
      this.passwordHash = passwordHash;      
   }      
   
   public boolean isEnabled()
   {
      return enabled;
   }


   public void setEnabled(boolean enabled)
   {
      this.enabled = enabled;      
   }   

   @ManyToMany(targetEntity = MemberAccount.class)
   @JoinTable(name = "AccountMembership", 
         joinColumns = @JoinColumn(name = "AccountId"),
         inverseJoinColumns = @JoinColumn(name = "MemberOf")
      )
   public Set<MemberAccount> getMemberships()
   {
      return memberships;
   }
   
   public void setMemberships(Set<MemberAccount> memberships)
   {
      this.memberships = memberships;
   }
   
   @OneToOne
   @JoinColumn(name = "MEMBER_ID")
   public Member getMember()
   {
      return member;
   }
   
   public void setMember(Member member)
   {
      this.member = member;
   }
}

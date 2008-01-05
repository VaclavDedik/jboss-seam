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
import org.jboss.seam.security.management.UserAccount;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "username"))
public class MemberAccount extends UserAccount implements Serializable
{
   private static final long serialVersionUID = 6368734442192368866L;
   
   private Integer accountId;
   private String username;
   private String passwordHash;
   private boolean enabled;   
   private AccountType accountType;
   private Set<UserAccount> memberships;
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
   @Override
   public String getUsername()
   {
      return username;
   }
   
   @Override
   public void setUsername(String username)
   {
      this.username = username;
   }
   
   @Override   
   public String getPasswordHash()
   {
      return passwordHash;
   }
   
   @Override
   public void setPasswordHash(String passwordHash)
   {
      this.passwordHash = passwordHash;      
   }   
   
   @Override
   public AccountType getAccountType()
   {
      return accountType;
   }
   
   @Override
   public void setAccountType(AccountType accountType)
   {
      this.accountType = accountType;
   }
   
   @Override
   public boolean isEnabled()
   {
      return enabled;
   }


   @Override
   public void setEnabled(boolean enabled)
   {
      this.enabled = enabled;      
   }   

   @ManyToMany(targetEntity = MemberAccount.class)
   @JoinTable(name = "AccountMembership", 
         joinColumns = @JoinColumn(name = "AccountId"),
         inverseJoinColumns = @JoinColumn(name = "MemberOf")
      )
   @Override
   public Set<UserAccount> getMemberships()
   {
      return memberships;
   }
   
   @Override
   public void setMemberships(Set<UserAccount> memberships)
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

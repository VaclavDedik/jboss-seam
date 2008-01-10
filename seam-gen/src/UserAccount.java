package @modelPackage@;

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
public class UserAccount extends org.jboss.seam.security.management.UserAccount implements Serializable
{
   private static final long serialVersionUID = 6368734442192368866L;
   
   private Integer accountId;
   private String username;
   private String passwordHash;
   private boolean enabled;   
   private AccountType accountType;
   private Set<org.jboss.seam.security.management.UserAccount> memberships;
   
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

   @ManyToMany(targetEntity = UserAccount.class)
   @JoinTable(name = "AccountMembership", 
         joinColumns = @JoinColumn(name = "AccountId"),
         inverseJoinColumns = @JoinColumn(name = "MemberOf")
      )
   @Override
   public Set<org.jboss.seam.security.management.UserAccount> getMemberships()
   {
      return memberships;
   }
   
   @Override
   public void setMemberships(Set<org.jboss.seam.security.management.UserAccount> memberships)
   {
      this.memberships = memberships;
   }
}

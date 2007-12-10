package org.jboss.seam.security.management;

import java.io.Serializable;
import java.util.Set;

/**
 * Abstract base class for user/role accounts.  This class should be extended
 * to create a concrete JPA/Hibernate implementation. The user has no access to
 * this class via the identity management API. 
 *  
 * @author Shane Bryzak
 */
public abstract class UserAccount implements Serializable
{
   public enum AccountType {user, role}
     
   public abstract String getUsername();   
   public abstract void setUsername(String username);
   
   public abstract String getPasswordHash();   
   public abstract void setPasswordHash(String passwordHash);

   public abstract boolean isEnabled();   
   public abstract void setEnabled(boolean enabled);
   
   public abstract AccountType getAccountType();   
   public abstract void setAccountType(AccountType accountType);
   
   public abstract Set<UserAccount> getMemberships();
   public abstract void setMemberships(Set<UserAccount> memberships);
   
   @Override
   public boolean equals(Object value)
   {
      if (!(value instanceof UserAccount))
      {
         return false;
      }
      
      UserAccount other = (UserAccount) value;      
      
      if (other.getUsername() == null && this.getUsername() == null)
      {
         return hashCode() == other.hashCode();
      }
      else
      {
         return getUsername() == null ? false : getUsername().equals(other.getUsername());
      }
   }
   
   @Override
   public int hashCode()
   {
      return getUsername() != null ? getUsername().hashCode() : super.hashCode();
   }
}

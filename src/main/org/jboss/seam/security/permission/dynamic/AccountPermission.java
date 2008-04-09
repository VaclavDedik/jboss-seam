package org.jboss.seam.security.permission.dynamic;

import org.jboss.seam.security.permission.AccountType;

/**
 * Abstract base class for persistence of user/role permissions.  This class should be extended
 * to create a concrete JPA/Hibernate implementation. 
 *  
 * @author Shane Bryzak
 */
public abstract class AccountPermission
{  
   public abstract String getTarget();
   public abstract void setTarget(String target);
   
   public abstract String getAction();
   public abstract void setAction(String action);
   
   public abstract String getAccount();
   public abstract void setAccount(String account);
   
   public abstract AccountType getAccountType();
   public abstract void setAccountType(AccountType accountType);
}

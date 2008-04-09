package org.jboss.seam.security.permission.acl;

import org.jboss.seam.security.permission.AccountType;

public abstract class AclPermission
{   
   public abstract String getObjectId();
   public abstract void setObjectId(String objectId);
   
   public abstract String getIdentifier();
   public abstract void setIdentifier(String identifier);
   
   public abstract String getAccount();
   public abstract void setAccount(String account);
   
   public abstract AccountType getAccountType();
   public abstract void setAccountType(AccountType accountType);
   
   public abstract long getPermissions();
   public abstract void setPermissions(long permissions);
}

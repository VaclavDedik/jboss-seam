package org.jboss.seam.security.management;

/**
 * A wrapper that is inserted into the working memory for rule-based permissions. 
 * 
 * @author Shane Bryzak
 */
public class SecurityContext
{  
   private UserAccount userAccount;
   
   public UserAccount getUserAccount()
   {
      return userAccount;
   }
   
   public void setUserAccount(UserAccount userAccount)
   {
      this.userAccount = userAccount;
   }
}

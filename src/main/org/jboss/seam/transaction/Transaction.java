package org.jboss.seam.transaction;

import static org.jboss.seam.annotations.Install.BUILT_IN;

import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.util.EJB;
import org.jboss.seam.util.Naming;

/**
 * Abstracts all possible transaction management APIs
 * behind a JTA-compatible interface.
 * 
 * @author Mike Youngstrom
 * @author Gavin King
 * 
 */
@Name("org.jboss.seam.transaction.transaction")
@Scope(ScopeType.STATELESS)
@Install(precedence=BUILT_IN)
@BypassInterceptors
public class Transaction
{

   private static final String STANDARD_USER_TRANSACTION_NAME = "java:comp/UserTransaction";

   private static String userTransactionName = "UserTransaction";

   public static void setUserTransactionName(String name)
   {
      userTransactionName = name;
   }

   public static String getUserTransactionName()
   {
      return userTransactionName;
   }
   
   public static UserTransaction instance()
   {
      return (UserTransaction) Component.getInstance(Transaction.class, ScopeType.STATELESS);
   }
   
   @Unwrap
   public UserTransaction getTransaction() throws NamingException
   {
      try
      {
         return createUTTransaction();
      }
      catch (NameNotFoundException nnfe)
      {
         try
         {
            return createEJBTransaction();
         }
         catch (NameNotFoundException nnfe2)
         {
            return createNoTransaction();
         }
      }
   }

   protected UserTransaction createNoTransaction()
   {
      return new NoTransaction();
   }

   protected UserTransaction createEJBTransaction() throws NamingException
   {
      return new EJBTransaction( EJB.getEJBContext() );
   }

   protected UserTransaction createUTTransaction() throws NamingException
   {
      return new UTTransaction( getUserTransaction() );
   }

   protected javax.transaction.UserTransaction getUserTransaction() throws NamingException
   {
      try
      {
         return (javax.transaction.UserTransaction) Naming.getInitialContext().lookup(userTransactionName);
      }
      catch (NameNotFoundException nnfe)
      {
         return (javax.transaction.UserTransaction) Naming.getInitialContext().lookup(STANDARD_USER_TRANSACTION_NAME);
      }
   }

}

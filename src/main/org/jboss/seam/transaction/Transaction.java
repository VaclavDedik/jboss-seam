package org.jboss.seam.transaction;

import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.util.Stack;

import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.transaction.Synchronization;

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
 * Supports injection of a Seam UserTransaction object that
 * wraps the current JTA transaction or EJB container managed
 * transaction. This base implementation does not have access
 * to the JTA TransactionManager, so it is not fully aware
 * of container managed transaction lifecycle, and is not
 * able to register Synchronizations with a container managed 
 * transaction.
 * 
 * @author Mike Youngstrom
 * @author Gavin King
 * 
 */
@Name("org.jboss.seam.transaction.transaction")
@Scope(ScopeType.EVENT)
@Install(precedence=BUILT_IN)
@BypassInterceptors
public class Transaction
{

   private static final String STANDARD_USER_TRANSACTION_NAME = "java:comp/UserTransaction";

   private static String userTransactionName = "UserTransaction";

   protected Stack<SynchronizationRegistry> synchronizations = new Stack<SynchronizationRegistry>();
   
   public void afterBegin()
   {
      synchronizations.push( new SynchronizationRegistry() );
   }
   
   protected void afterCommit(boolean success)
   {
      synchronizations.pop().afterTransactionCompletion(success);
   }
   
   protected void afterRollback()
   {
      synchronizations.pop().afterTransactionCompletion(false);
   }
   
   protected void beforeCommit()
   {
      synchronizations.peek().beforeTransactionCompletion();
   }
   
   protected void registerSynchronization(Synchronization sync)
   {
      if (synchronizations==null)
      {
         throw new IllegalStateException("no transaction active, or the transaction is a CMT (try installing <transaction:ejb-transaction/>)");
      }
      else
      {
         synchronizations.peek().registerSynchronization(sync);
      }
   }
   
   protected boolean isAwareOfContainerTransactions()
   {
      return false;
   }
   
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
      return (UserTransaction) Component.getInstance(Transaction.class, ScopeType.EVENT);
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
      return new CMTTransaction( EJB.getEJBContext(), this );
   }

   protected UserTransaction createUTTransaction() throws NamingException
   {
      return new UTTransaction( getUserTransaction(), this );
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

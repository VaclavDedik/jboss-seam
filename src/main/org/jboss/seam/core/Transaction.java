package org.jboss.seam.core;

import static javax.transaction.Status.STATUS_ACTIVE;
import static javax.transaction.Status.STATUS_MARKED_ROLLBACK;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.TransactionException;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.util.EJB;
import org.jboss.seam.util.Naming;

/**
 * Wraps JTA and EJBContext transaction management into a single Seam component
 * that implements UserTransaction. This component can then be extended for more
 * specific transaction management situations.
 * 
 * @author Mike Youngstrom
 * 
 */
@Name("org.jboss.seam.core.transaction")
@Scope(ScopeType.STATELESS)
@Install(precedence=BUILT_IN)
public class Transaction implements UserTransaction
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
   
   public static Transaction instance()
   {
      if (!Contexts.isApplicationContextActive())
      {
         throw new IllegalStateException("No application context active, cannot obtain Transaction component");
      }
      return (Transaction) Component.getInstance(Transaction.class, ScopeType.APPLICATION);
   }

   private UserTransaction getUserTransaction() throws NamingException
   {
      try
      {
         return (UserTransaction) Naming.getInitialContext().lookup(userTransactionName);
      }
      catch (NameNotFoundException nnfe)
      {
         return (UserTransaction) Naming.getInitialContext().lookup(STANDARD_USER_TRANSACTION_NAME);
      }
   }

   public void begin() throws NotSupportedException, SystemException
   {
      try
      {
         getUserTransaction().begin();
      }
      catch (NamingException ne)
      {
         throw new TransactionException("Unable to begin UserTransaction", ne);
      }

   }

   public void commit() throws RollbackException, HeuristicMixedException,
            HeuristicRollbackException, SecurityException, IllegalStateException, SystemException
   {
      try
      {
         getUserTransaction().commit();
      }
      catch (NamingException ne)
      {
         throw new TransactionException("Unable to commit UserTransaction", ne);
      }

   }

   public int getStatus() throws SystemException
   {
      try
      {
         return getUserTransaction().getStatus();
      }
      catch (NamingException ne)
      {
         try
         {
            if ( !EJB.getEJBContext().getRollbackOnly() )
            {
               return Status.STATUS_ACTIVE;
            }
            else
            {
               return Status.STATUS_MARKED_ROLLBACK;
            }
         }
         catch (NamingException e)
         {
            throw new TransactionException("Unable to get UserTransaction status", ne);
         }
         catch (IllegalStateException e)
         {
            return Status.STATUS_NO_TRANSACTION;
         }
      }
   }

   public void rollback() throws IllegalStateException, SecurityException, SystemException
   {
      try
      {
         getUserTransaction().rollback();
      }
      catch (NamingException ne)
      {
         throw new TransactionException("Unable to rollback UserTransaction", ne);
      }
   }

   public void setRollbackOnly() throws IllegalStateException, SystemException
   {
      try
      {
         getUserTransaction().setRollbackOnly();
      }
      catch (NamingException ne)
      {
         try
         {
            EJB.getEJBContext().setRollbackOnly();
         }
         catch (NamingException e)
         {
            throw new TransactionException("Unable to set UserTransaction or EJBContext to rollback only", ne);
         }
      }

   }

   public void setTransactionTimeout(int timeout) throws SystemException
   {
      try
      {
         getUserTransaction().setTransactionTimeout(timeout);
      }
      catch (NamingException ne)
      {
         throw new TransactionException("Unable set transaction timeout", ne);
      }

   }
   
   public boolean isActive() throws SystemException
   {
      return getStatus() == STATUS_ACTIVE;
   }

   public boolean isActiveOrMarkedRollback() throws SystemException
   {
      int status = getStatus();
      return status == STATUS_ACTIVE || status == STATUS_MARKED_ROLLBACK;
   }

   public boolean isMarkedRollback() throws SystemException
   {
      return getStatus() == STATUS_MARKED_ROLLBACK;
   }

}

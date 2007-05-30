package org.jboss.seam.transaction;

import javax.ejb.EJBContext;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;

/**
 * Wraps EJBContext transaction management in a
 * UserTransaction interface.
 * 
 * @author Mike Youngstrom
 * @author Gavin King
 * 
 */
public class EJBTransaction extends UserTransaction
{
   
   private EJBContext ejbContext;
   

   public EJBTransaction(EJBContext ejbContext)
   {
      this.ejbContext = ejbContext;
   }

   public void begin() throws NotSupportedException, SystemException
   {
      throw new UnsupportedOperationException();
   }

   public void commit() throws RollbackException, HeuristicMixedException,
            HeuristicRollbackException, SecurityException, IllegalStateException, SystemException
   {
      throw new UnsupportedOperationException();
   }

   public int getStatus() throws SystemException
   {
      try
      {
         if ( !ejbContext.getRollbackOnly() )
         {
            return Status.STATUS_ACTIVE;
         }
         else
         {
            return Status.STATUS_MARKED_ROLLBACK;
         }
      }
      catch (IllegalStateException ise)
      {
         return Status.STATUS_NO_TRANSACTION;
      }
   }

   public void rollback() throws IllegalStateException, SecurityException, SystemException
   {
      throw new UnsupportedOperationException();
   }

   public void setRollbackOnly() throws IllegalStateException, SystemException
   {
      ejbContext.setRollbackOnly();
   }

   public void setTransactionTimeout(int timeout) throws SystemException
   {
      throw new UnsupportedOperationException();
   }

}

package org.jboss.seam.transaction;

import static javax.transaction.Status.STATUS_ACTIVE;
import static javax.transaction.Status.STATUS_MARKED_ROLLBACK;

import javax.transaction.SystemException;

/**
 * Extends the standard UserTransaction interface with a couple 
 * of helpful methods.
 * 
 * @author Gavin King
 * 
 */
public abstract class UserTransaction implements javax.transaction.UserTransaction
{
   
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

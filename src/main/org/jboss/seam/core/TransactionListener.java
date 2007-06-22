package org.jboss.seam.core;

import javax.transaction.Synchronization;

/**
 * API for scheduling work that happens in concert
 * with the JTA transaction commit.
 * 
 * @author Gavin King
 *
 */
public interface TransactionListener
{
   /**
    * Schedule an event that will be processed after the
    * current transaction completes.
    * 
    * @param type the event type
    * @param parameters parameters to be passes to the listener method
    */
   public void scheduleEvent(String type, Object... parameters);
   /**
    * Register a Synchronization with the current transaction
    */
   public void registerSynchronization(Synchronization sync);
   /**
    * Called by Seam before it commits a transaction
    */
   public void beforeSeamManagedTransactionCompletion();
   /**
    * Called by Seam after it commits a transaction
    * @param success was the commit successful?
    */
   public void afterSeamManagedTransactionCompletion(boolean success);
}

package org.jboss.seam.transaction;

import javax.transaction.Synchronization;

/**
 * Interface for registering transaction synchronizations
 * 
 * @author Gavin King
 *
 */
public interface Synchronizations
{
   public void afterBegin();
   public void afterCommit(boolean success);
   public void afterRollback();
   public void beforeCommit();
   public void registerSynchronization(Synchronization sync);
   public boolean isAwareOfContainerTransactions();
}
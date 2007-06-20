package org.jboss.seam.core;

import javax.transaction.Synchronization;

public interface TransactionListener
{
   public void scheduleEvent(String type, Object... parameters);
   public void registerSynchronization(Synchronization sync);
}

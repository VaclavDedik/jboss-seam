package org.jboss.seam.core;

import javax.ejb.Local;
import javax.transaction.Synchronization;

@Local
public interface LocalTransactionListener
{
   public void scheduleEvent(String type, Object... parameters);
   public void registerSynchronization(Synchronization sync);
   public void destroy();
}

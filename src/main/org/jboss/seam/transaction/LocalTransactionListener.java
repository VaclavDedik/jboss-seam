package org.jboss.seam.transaction;

import javax.ejb.Local;

@Local
public interface LocalTransactionListener
{
   public void scheduleEvent(String type, Object... parameters);
   public void destroy();
}

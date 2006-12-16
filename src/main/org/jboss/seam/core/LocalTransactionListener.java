package org.jboss.seam.core;

import javax.ejb.Local;

@Local
public interface LocalTransactionListener
{
   public void scheduleEvent(String type, Object... parameters);
   public void destroy();
}

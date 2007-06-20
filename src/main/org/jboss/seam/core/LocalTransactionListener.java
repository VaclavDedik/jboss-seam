package org.jboss.seam.core;

import javax.ejb.Local;


@Local
public interface LocalTransactionListener extends TransactionListener
{
   public void destroy();
}

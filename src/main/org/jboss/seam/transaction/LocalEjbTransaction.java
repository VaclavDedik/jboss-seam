package org.jboss.seam.transaction;

import javax.ejb.Local;
import javax.naming.NamingException;

/**
 * Local interface for EjbTransaction
 * @author Gavin King
 *
 */
@Local
public interface LocalEjbTransaction 
{
   public UserTransaction getTransaction() throws NamingException;
   public void destroy();
}

package org.jboss.seam.core;

import static org.jboss.seam.annotations.Install.FRAMEWORK;

import java.rmi.RemoteException;

import javax.ejb.EJBException;
import javax.ejb.Remove;
import javax.ejb.SessionSynchronization;
import javax.ejb.Stateful;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * A TransactionListener that works in an EJB container.
 * Once all appservers support the new EE5 APIs, this can 
 * be removed.
 * 
 * @author Gavin King
 *
 */
@Stateful
@Scope(ScopeType.EVENT)
@Name("org.jboss.seam.core.transactionListener")
@Install(precedence=FRAMEWORK, value=false)
public class EjbTransactionListener extends BasicTransactionListener 
      implements LocalTransactionListener, SessionSynchronization
{
   public void afterCompletion(boolean success) throws EJBException, RemoteException
   {
      super.afterTransactionCompletion(success);
   }

   public void beforeCompletion() throws EJBException, RemoteException
   {
      super.beforeTransactionCompletion();
   }
   
   public void afterBegin() throws EJBException, RemoteException {}
   
   @Override
   public void afterSeamManagedTransactionCompletion(boolean success)
   {
      //nopop, let JTA call
   }
   
   @Override
   public void beforeSeamManagedTransactionCompletion() 
   {
      //nopop, let JTA call
   }
   
   @Remove
   public void destroy() {}

}

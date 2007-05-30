package org.jboss.seam.transaction;

import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJBException;
import javax.ejb.Remove;
import javax.ejb.SessionSynchronization;
import javax.ejb.Stateful;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Events;

/**
 * Temporary solution for getting JTA transaction lifecycle
 * callbacks. Once all appservers support the new EE5 APIs,
 * this will be removed.
 * 
 * @author Gavin King
 *
 */
@Stateful
@Scope(ScopeType.EVENT)
@Name("org.jboss.seam.transaction.transactionListener")
@Install(value=false, precedence=BUILT_IN)
public class TransactionListener implements LocalTransactionListener, SessionSynchronization
{
   static class Event
   {
      private String type;
      private Object[] parameters;
      
      public Event(String type, Object[] parameters)
      {
         this.type = type;
         this.parameters = parameters;
      }
      public void call()
      {
         Events.instance().raiseEvent(type, parameters);
      }
   }

   private List<Event> events = new ArrayList<Event>();
   
   public static LocalTransactionListener instance()
   {
      if ( !Contexts.isEventContextActive() )
      {
         throw new IllegalStateException("no event context active");
      }
      return (LocalTransactionListener) Component.getInstance(TransactionListener.class, ScopeType.EVENT);         
   }
   
   public void afterBegin() throws EJBException, RemoteException
   {
   }
   
   public void scheduleEvent(String type, Object... parameters)
   {
      events.add( new Event(type, parameters) );
   }

   public void afterCompletion(boolean success) throws EJBException, RemoteException
   {
      try
      {
         if (success)
         {
            for (Event event: events) event.call();
         }
      }
      finally
      {
         events.clear();
      }
   }

   public void beforeCompletion() throws EJBException, RemoteException
   {
   }
   
   @Remove @Destroy
   public void destroy() {}

}

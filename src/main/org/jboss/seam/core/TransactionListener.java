package org.jboss.seam.core;

import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJBException;
import javax.ejb.Remove;
import javax.ejb.SessionSynchronization;
import javax.ejb.Stateful;
import javax.transaction.Status;
import javax.transaction.Synchronization;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.Log;

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
@Name("org.jboss.seam.core.transactionListener")
@Install(value=false, precedence=BUILT_IN)
public class TransactionListener implements LocalTransactionListener, SessionSynchronization
{
   private static @Logger Log log;
   
   static class Event implements Serializable
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
   private List<Synchronization> synchronizations = new ArrayList<Synchronization>();
   
   public static LocalTransactionListener instance()
   {
      if ( !Contexts.isEventContextActive() )
      {
         throw new IllegalStateException("no event context active");
      }
      return (LocalTransactionListener) Component.getInstance(TransactionListener.class, ScopeType.EVENT);         
   }
   
   public void afterBegin() throws EJBException, RemoteException {}
   
   public void scheduleEvent(String type, Object... parameters)
   {
      events.add( new Event(type, parameters) );
   }
   
   public void registerSynchronization(Synchronization sync)
   {
      synchronizations.add(sync);
   }

   public void afterCompletion(boolean success) throws EJBException, RemoteException
   {
      Events.instance().raiseEvent("org.jboss.seam.afterTransactionCompletion", success);
      for (Synchronization sync: synchronizations)
      {
         try
         {
            sync.afterCompletion(success ? Status.STATUS_COMMITTED : Status.STATUS_ROLLEDBACK);
         }
         catch (Exception e)
         {
            log.error("Exception processing transaction Synchronization after completion", e);
         }
      }
      synchronizations.clear();
      if (success)
      {
         for (Event event: events)
         {
            try
            {
               event.call();
            }
            catch (Exception e)
            {
               log.error("Exception processing transaction success event", e);
            }
         }
         events.clear();
      }
   }

   public void beforeCompletion() throws EJBException, RemoteException
   {
      Events.instance().raiseEvent("org.jboss.seam.beforeTransactionCompletion");
      for (Synchronization sync: synchronizations)
      {
         try
         {
            sync.beforeCompletion();
         }
         catch (Exception e)
         {
            log.error("Exception processing transaction Synchronization before completion", e);
         }
      }
   }
   
   @Remove
   public void destroy() {}

}

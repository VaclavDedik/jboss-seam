package org.jboss.seam.core;

import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
 * Base implementation of the TransactionListener interface.
 * This one only works for Seam-managed transactions, but
 * works in any environment.
 * 
 * @see EjbTransactionListener
 * 
 * @author Gavin King
 *
 */
@Scope(ScopeType.EVENT)
@Name("org.jboss.seam.core.transactionListener")
@Install(precedence=BUILT_IN, classDependencies="javax.transaction.Status")
public class BasicTransactionListener implements TransactionListener
{
   private static @Logger Log log;
   
   private List<Event> events = new ArrayList<Event>();
   private List<Synchronization> synchronizations = new ArrayList<Synchronization>();
   
   private static class Event implements Serializable
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

   public static TransactionListener instance()
   {
      if ( !Contexts.isEventContextActive() )
      {
         throw new IllegalStateException("no event context active");
      }
      return (TransactionListener) Component.getInstance(BasicTransactionListener.class, ScopeType.EVENT);         
   }
      
   public void scheduleEvent(String type, Object... parameters)
   {
      events.add( new Event(type, parameters) );
   }
   
   public void registerSynchronization(Synchronization sync)
   {
      synchronizations.add(sync);
   }

   protected void afterTransactionCompletion(boolean success)
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
   
   protected void beforeTransactionCompletion()
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
   
   public void beforeSeamManagedTransactionCompletion()
   {
      beforeTransactionCompletion();
   }
   
   public void afterSeamManagedTransactionCompletion(boolean success)
   {
      afterTransactionCompletion(success);
   }
   
}

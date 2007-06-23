package org.jboss.seam.transaction;

import static org.jboss.seam.annotations.Install.FRAMEWORK;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.SystemException;

import org.hibernate.Session;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.core.Expressions.ValueExpression;

/**
 * Support for the Hibernate Transaction API.
 * 
 * Adapts Hibernate transaction management to a
 * UserTransaction interface.
 * 
 * @author Gavin King
 * 
 */
@Name("org.jboss.seam.transaction.transaction")
@Scope(ScopeType.EVENT)
@Install(value=false, precedence=FRAMEWORK)
@BypassInterceptors
public class HibernateTransaction extends UserTransaction
{

   private ValueExpression<Session> session;
   private Session currentSession;
   private boolean rollbackOnly; //Hibernate Transaction doesn't have a "rollback only" state
   
   private org.hibernate.Transaction getDelegate()
   {
      if (currentSession==null)
      {
         //should never occur
         throw new IllegalStateException("session is null");
      }
      return currentSession.getTransaction();
   }

   private void initSession()
   {
      currentSession = session.getValue();
      if (currentSession==null)
      {
         throw new IllegalStateException("session was null: " + session.getExpressionString());
      }
   }

   public void begin() throws NotSupportedException, SystemException
   {
      assertNotActive();
      initSession();
      try
      {
         getDelegate().begin();
      }
      catch (RuntimeException re)
      {
         clearSession();
         throw re;
      }
   }

   public void commit() throws RollbackException, HeuristicMixedException,
            HeuristicRollbackException, SecurityException, IllegalStateException, SystemException
   {
      assertActive();
      try
      {
         if (rollbackOnly)
         {
            getDelegate().rollback();
            throw new RollbackException();
         }
         else
         {
            getDelegate().commit();
         }
      }
      finally
      {
         clearSession();
      }
   }

   public int getStatus() throws SystemException
   {
      if (rollbackOnly)
      {
         return Status.STATUS_MARKED_ROLLBACK;
      }
      else if ( isSessionSet() && getDelegate().isActive() )
      {
         return Status.STATUS_ACTIVE;
      }
      else
      {
         return Status.STATUS_NO_TRANSACTION;
      }
   }

   public void rollback() throws IllegalStateException, SecurityException, SystemException
   {
      //TODO: translate exceptions that occur into the correct JTA exception
      assertActive();
      try
      {
         getDelegate().rollback();
      }
      finally
      {
         clearSession();
      }
   }

   public void setRollbackOnly() throws IllegalStateException, SystemException
   {
      assertActive();
      rollbackOnly = true;
   }

   public void setTransactionTimeout(int timeout) throws SystemException
   {
      assertActive();
      getDelegate().setTimeout(timeout);
   }
   
   private boolean isSessionSet()
   {
      return currentSession!=null;
   }
   
   private void clearSession()
   {
      currentSession = null;
   }

   private void assertActive()
   {
      if ( !isSessionSet() )
      {
         throw new IllegalStateException("transaction is not active");
      }
   }

   private void assertNotActive() throws NotSupportedException
   {
      //TODO: translate exceptions that occur into the correct JTA exception
      if ( isSessionSet() )
      {
         throw new NotSupportedException("transaction is already active");
      }
   }
   
   @Override
   public boolean isConversationContextRequired()
   {
      return true;
   }

   public ValueExpression<Session> getSession()
   {
      return session;
   }

   public void setSession(ValueExpression<Session> entityManager)
   {
      this.session = entityManager;
   }

}

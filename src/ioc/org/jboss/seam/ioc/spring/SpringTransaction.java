package org.jboss.seam.ioc.spring;

import static org.jboss.seam.annotations.Install.FRAMEWORK;

import javax.persistence.EntityManager;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Status;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.ServletLifecycle;
import org.jboss.seam.core.Expressions.ValueExpression;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.transaction.AbstractUserTransaction;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.context.support.WebApplicationContextUtils;

@Name("org.jboss.seam.transaction.transaction")
@Scope(ScopeType.EVENT)
@Install(value = false, precedence = FRAMEWORK)
@BypassInterceptors
public class SpringTransaction extends AbstractUserTransaction
{
   private static final LogProvider log = Logging.getLogProvider(SpringTransaction.class);

   private ValueExpression<PlatformTransactionManager> platformTransactionManager;

   private String platformTransactionManagerName;

   private DefaultTransactionDefinition definition = new DefaultTransactionDefinition();

   private boolean conversationContextRequired = true;

   private TransactionStatus currentTransaction;

   private Boolean joinTransaction;

   @Override
   public void registerSynchronization(final Synchronization sync)
   {
      if (TransactionSynchronizationManager.isSynchronizationActive())
      {
         TransactionSynchronizationManager.registerSynchronization(new JtaSpringSynchronizationAdapter(sync));
      }
      else
      {
         throw new IllegalStateException("TransactionSynchronization not available with this Spring Transaction Manager");
      }
   }

   public void begin() throws NotSupportedException, SystemException
   {
      if (TransactionSynchronizationManager.isActualTransactionActive())
      {
         throw new NotSupportedException("A Spring transaction is already active.");
      }
      currentTransaction = getPlatformTransactionManagerRequired().getTransaction(definition);
   }

   /**
    * Obtains a PlatformTransactionManager from either the name or expression
    * specified.
    *
    * @return
    */
   protected PlatformTransactionManager getPlatformTransactionManager()
   {
      if (((platformTransactionManagerName == null || "".equals(platformTransactionManagerName)) && platformTransactionManager == null) || (platformTransactionManagerName != null && !"".equals(platformTransactionManagerName)) && platformTransactionManager != null)
      {
         throw new IllegalArgumentException("When configuring spring:spring-transaction you must specify either platformTransactionManager or platformTransactionManagerName.");
      }
      if ((platformTransactionManagerName == null || "".equals(platformTransactionManagerName)))
      {
         return platformTransactionManager.getValue();
      }
      BeanFactory beanFactory = findBeanFactory();
      if (beanFactory == null)
      {
         log.debug("BeanFactory either not found or not yet available.");
         return null;
      }
      PlatformTransactionManager ptm = (PlatformTransactionManager) beanFactory.getBean(platformTransactionManagerName);
      return ptm;
   }

   private PlatformTransactionManager getPlatformTransactionManagerRequired() {
      PlatformTransactionManager ptm = getPlatformTransactionManager();
      if (ptm == null)
      {
         throw new IllegalStateException("Unable to find PlatformTransactionManager");
      }
      return ptm;
   }

   /**
    * Attempts to find a BeanFactory and return the instance found.
    *
    * @return BeanFactory or null if non found.
    */
   protected BeanFactory findBeanFactory()
   {
      return WebApplicationContextUtils.getWebApplicationContext(ServletLifecycle.getServletContext());
   }

   public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException
   {
      assertActive();
      try
      {
         getPlatformTransactionManagerRequired().commit(currentTransaction);
      }
      finally
      {
         currentTransaction = null;
      }
   }

   public int getStatus() throws SystemException
   {
      PlatformTransactionManager ptm = getPlatformTransactionManager();
      if (ptm == null)
      {
         return Status.STATUS_NO_TRANSACTION;
      }
      if (TransactionSynchronizationManager.isActualTransactionActive())
      {
         TransactionStatus transaction = null;
         try
         {
            if (currentTransaction == null)
            {
               transaction = ptm.getTransaction(definition);
               if (transaction.isNewTransaction())
               {
                  return Status.STATUS_COMMITTED;
               }
            }
            else
            {
               transaction = currentTransaction;
            }
            // If SynchronizationManager things it has an active transaction but
            // our transaction is a new one
            // then we must be in the middle of committing
            if (transaction.isCompleted())
            {
               if (transaction.isRollbackOnly())
               {
                  return Status.STATUS_ROLLEDBACK;
               }
               return Status.STATUS_COMMITTED;
            }
            else
            {
               if (transaction.isRollbackOnly())
               {
                  return Status.STATUS_MARKED_ROLLBACK;
               }
               return Status.STATUS_ACTIVE;
            }
         }
         finally
         {
            if (currentTransaction == null)
            {
               ptm.commit(transaction);
            }
         }
      }
      return Status.STATUS_NO_TRANSACTION;
   }

   public void rollback() throws IllegalStateException, SecurityException, SystemException
   {
      assertActive();
      try
      {
         getPlatformTransactionManagerRequired().rollback(currentTransaction);
      }
      finally
      {
         currentTransaction = null;
      }
   }

   /**
    *
    */
   private void assertActive()
   {
      if (!TransactionSynchronizationManager.isActualTransactionActive() || currentTransaction == null)
      {
         throw new IllegalStateException("No transaction currently active that Seam started." + "Seam should only be able to committ or rollback transactions it started.");
      }
   }

   public void setRollbackOnly() throws IllegalStateException, SystemException
   {
      if (!TransactionSynchronizationManager.isActualTransactionActive())
      {
         throw new IllegalStateException("No Spring Transaction is currently available.");
      }
      TransactionStatus transaction = null;
      PlatformTransactionManager ptm = getPlatformTransactionManagerRequired();
      try
      {
         if (currentTransaction == null)
         {
            transaction = ptm.getTransaction(definition);
         }
         else
         {
            transaction = currentTransaction;
         }
         transaction.setRollbackOnly();
      }
      finally
      {
         if (currentTransaction == null)
         {
            ptm.commit(transaction);
         }
      }
   }

   public void setTransactionTimeout(int timeout) throws SystemException
   {
      if (TransactionSynchronizationManager.isActualTransactionActive())
      {
         // cannot set timeout on already running transaction
         return;
      }
      definition.setTimeout(timeout);
   }

   @Override
   public void enlist(EntityManager entityManager) throws SystemException
   {
      if (joinTransaction == null)
      {
         // If not set attempt to detect if we should join or not
         if (!(getPlatformTransactionManagerRequired() instanceof JpaTransactionManager))
         {
            super.enlist(entityManager);
         }
      }
      else if (joinTransaction)
      {
         super.enlist(entityManager);
      }
   }

   @Destroy
   public void cleanupCurrentTransaction()
   {
      if (currentTransaction != null)
      {
         try
         {
            log.debug("Attempting to rollback left over transaction.  Should never be called.");
            getPlatformTransactionManagerRequired().rollback(currentTransaction);
         }
         catch (Throwable e)
         {
            // ignore
         }
      }
   }

   public void setPlatformTransactionManager(ValueExpression<PlatformTransactionManager> platformTransactionManager)
   {
      this.platformTransactionManager = platformTransactionManager;
   }

   public void setPlatformTransactionManagerName(String platformTransactionManagerName)
   {
      this.platformTransactionManagerName = platformTransactionManagerName;
   }

   @Override
   public boolean isConversationContextRequired()
   {
      return conversationContextRequired;
   }

   public void setConversationContextRequired(boolean conversationContextRequired)
   {
      this.conversationContextRequired = conversationContextRequired;
   }

   public void setJoinTransaction(Boolean joinTransaction)
   {
      this.joinTransaction = joinTransaction;
   }

   public class JtaSpringSynchronizationAdapter extends TransactionSynchronizationAdapter
   {
      @Override
      public int getOrder()
      {
         return SeamLifecycleUtils.SEAM_LIFECYCLE_SYNCHRONIZATION_ORDER - 1;
      }

      private final Synchronization sync;

      public JtaSpringSynchronizationAdapter(Synchronization sync)
      {
         this.sync = sync;
      }

      @Override
      public void afterCompletion(int status)
      {
         sync.afterCompletion(convertSpringStatus(status));
      }

      @Override
      public void beforeCompletion()
      {
         sync.beforeCompletion();
      }

      private int convertSpringStatus(int springStatus)
      {
         switch (springStatus)
         {
         case TransactionSynchronization.STATUS_COMMITTED:
            return Status.STATUS_COMMITTED;
         case TransactionSynchronization.STATUS_ROLLED_BACK:
            return Status.STATUS_ROLLEDBACK;
         default:
            return Status.STATUS_UNKNOWN;
         }
      }
   }
}

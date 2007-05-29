package org.jboss.seam.ioc.spring;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TransactionRequiredException;

import org.jboss.seam.Component;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.ClassUtils;

/**
 * An EntityManagerFactory that defers creation and management of an
 * EntityManager to a Seam ManagedPersistenceContext.
 * 
 * @author Mike Youngstrom
 */
public class SeamManagedEntityManagerFactory implements EntityManagerFactory
{
   private static final LogProvider log = Logging
            .getLogProvider(SeamManagedEntityManagerFactory.class);

   private String persistenceContextName;

   private boolean closed = false;

   public SeamManagedEntityManagerFactory(String seamPersistenceContextName)
   {
      super();
      if (seamPersistenceContextName == null || "".equals(seamPersistenceContextName))
      {
         throw new IllegalArgumentException("persistenceContextName cannot be null");
      }
      this.persistenceContextName = seamPersistenceContextName;
   }

   public void close()
   {
      closed = true;
   }

   /**
    * Wraps the Seam ManagedPersistenceContext in a close suppressing proxy and
    * returns.
    */
   public EntityManager createEntityManager()
   {
      if (closed)
      {
         throw new IllegalStateException("EntityManagerFactory is closed");
      }
      log.debug("Returning a Seam Managed PC from createEntityManager()");
      SeamLifecycleUtils.beginTransactionalSeamCall();
      EntityManager em = (EntityManager) Component.getInstance(persistenceContextName);
      //An extended EntityManager may not join the current Transaction
      //because spring may think it's creating a new EntityManager that
      //will join the current transaction automatically instead of
      //getting an old EntityManager.
      if(TransactionSynchronizationManager.isActualTransactionActive()) {
         try {
            em.joinTransaction();
         }
         catch (TransactionRequiredException ex) {
            log.debug("Could not join JTA transaction because none was active", ex);
         }
      }
      return (EntityManager) Proxy.newProxyInstance(getClass().getClassLoader(), ClassUtils
               .getAllInterfaces(em), new SeamManagedPersistenceContextHandler(em));
   }

   public EntityManager createEntityManager(Map properties)
   {
      // Not really sure if I should throw an exception here or just ignore the Map
      throw new UnsupportedOperationException(
               "Cannot change properties of a Seam ManagedPersistenceContext this way.  "
                        + "This must be done on the ManagedPersistenceContext seam component.");
   }

   public boolean isOpen()
   {
      return !closed;
   }

   /**
    * EntityManager InvocationHandler used to correctly calls to close and
    * isOpen. We don't want Spring closing the SeamEntityManager only this
    * proxy.
    * 
    * @author Mike Youngstrom
    * 
    */
   public static class SeamManagedPersistenceContextHandler implements InvocationHandler
   {
      private static final LogProvider log = Logging
               .getLogProvider(SeamManagedPersistenceContextHandler.class);

      private EntityManager delegate;

      private boolean closed = false;

      public SeamManagedPersistenceContextHandler(EntityManager delegate)
      {
         super();
         this.delegate = delegate;
      }

      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
      {
         if (method.getName().equals("isOpen"))
         {
            return delegate.isOpen() && !closed;
         }
         if (!delegate.isOpen())
         {
            // Defer to delegate error if it's closed.
            try
            {
               return method.invoke(delegate, args);
            }
            catch (InvocationTargetException ex)
            {
               throw ex.getTargetException();
            }
         }
         if (closed)
         {
            throw new IllegalStateException("This EntityManager is closed.");
         }
         if (method.getName().equals("close"))
         {
            log.debug("Closing EntityManager Proxy.");
            closed = true;
            return null;
         }
         try
         {
            return method.invoke(delegate, args);
         }
         catch (InvocationTargetException ex)
         {
            throw ex.getTargetException();
         }
      }
   }
}

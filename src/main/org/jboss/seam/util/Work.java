package org.jboss.seam.util;

import static org.jboss.seam.ComponentType.JAVA_BEAN;
import static org.jboss.seam.util.EJB.APPLICATION_EXCEPTION;
import static org.jboss.seam.util.EJB.rollback;

import javax.transaction.Status;
import javax.transaction.UserTransaction;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.ApplicationException;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.transaction.Transaction;

/**
 * Performs work in a JTA transaction.
 * 
 * @author Gavin King
 */
public abstract class Work<T>
{
   private static final LogProvider log = Logging.getLogProvider(Work.class);
   
   protected abstract T work() throws Exception;
   
   protected boolean isNewTransactionRequired(boolean transactionActive)
   {
      return !transactionActive;
   }
   
   public final T workInTransaction() throws Exception
   {
      boolean transactionActive = Transaction.instance().isActiveOrMarkedRollback()
              || Transaction.instance().isRolledBack(); //TODO: temp workaround, what should we really do in this case??
      boolean newTransactionRequired = isNewTransactionRequired(transactionActive);
      UserTransaction userTransaction = newTransactionRequired ? Transaction.instance() : null;
      
      if (newTransactionRequired) 
      {
         log.debug("beginning transaction");
         userTransaction.begin();
      }
      
      try
      {
         T result = work();
         if (newTransactionRequired) 
         {
            if (Transaction.instance().isMarkedRollback())
            {
               log.debug("rolling back transaction");
               userTransaction.rollback(); 
            }
            else
            {
               log.debug("committing transaction");
               userTransaction.commit();
            }
         }
         return result;
      }
      catch (Exception e)
      {
         if (newTransactionRequired && userTransaction.getStatus() != Status.STATUS_NO_TRANSACTION && isRollbackRequired(e, true)) 
         {
            log.debug("rolling back transaction");
            userTransaction.rollback();
         }
         throw e;
      }
      
   }
   
   public static boolean isRollbackRequired(Exception e, boolean isJavaBean)
   {
      Class<? extends Exception> clazz = e.getClass();
      return ( isSystemException(e, isJavaBean, clazz) ) || 
            ( isJavaBean && clazz.isAnnotationPresent(APPLICATION_EXCEPTION) && rollback( clazz.getAnnotation(APPLICATION_EXCEPTION) ) ) ||
            ( clazz.isAnnotationPresent(ApplicationException.class) && clazz.getAnnotation(ApplicationException.class).rollback() );
   }

   private static boolean isSystemException(Exception e, boolean isJavaBean, Class<? extends Exception> clazz)
   {
      return isJavaBean && 
            (e instanceof RuntimeException) && 
            !clazz.isAnnotationPresent(APPLICATION_EXCEPTION) && 
            !clazz.isAnnotationPresent(ApplicationException.class) &&
            //TODO: this is hackish, maybe just turn off RollackInterceptor for @Converter/@Validator components
            !JSF.VALIDATOR_EXCEPTION.isInstance(e) &&
            !JSF.CONVERTER_EXCEPTION.isInstance(e);
   }
}

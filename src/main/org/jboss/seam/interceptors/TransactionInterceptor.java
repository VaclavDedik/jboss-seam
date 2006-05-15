package org.jboss.seam.interceptors;

import java.lang.reflect.AnnotatedElement;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.transaction.UserTransaction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.annotations.Around;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.util.Transactions;

@Around({RollbackInterceptor.class, BusinessProcessInterceptor.class, ConversationInterceptor.class})
public class TransactionInterceptor extends AbstractInterceptor
{
   private static final Log log = LogFactory.getLog(TransactionInterceptor.class);
   
   @AroundInvoke
   public Object doInTransactionIfNecessary(InvocationContext invocation) throws Exception
   {
      boolean begin = ( isTransactional( invocation.getMethod() ) || isTransactional( component.getBeanClass() ) ) &&
            !Transactions.isTransactionActiveOrMarkedRollback();
      UserTransaction userTransaction = begin ? Transactions.getUserTransaction() : null;

      if (begin) 
      {
         log.debug("beginning transaction");
         userTransaction.begin();
      }
      try
      {
         Object result = invocation.proceed();
         if (begin) 
         {
            log.debug("committing transaction");
            userTransaction.commit();
         }
         return result;
      }
      catch (Exception e)
      {
         if (begin) 
         {
            log.debug("rolling back transaction");
            userTransaction.rollback();
         }
         throw e;
      }
   }

   private static boolean isTransactional(AnnotatedElement element)
   {
      return element.isAnnotationPresent(Transactional.class);
   }

}

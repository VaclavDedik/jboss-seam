//$Id$
package org.jboss.seam.interceptors;

import java.util.Arrays;
import java.util.List;

import javax.ejb.AroundInvoke;
import javax.ejb.InvocationContext;

import org.jboss.seam.ComponentType;
import org.jboss.seam.annotations.Around;
import org.jboss.seam.annotations.Outcome;
import org.jboss.seam.annotations.Rollback;
import org.jboss.seam.util.Transactions;

/**
 * Automatically sets transactions to rollback.
 * 
 * @author Gavin King
 */
@Around(OutcomeInterceptor.class)
public class RollbackInterceptor extends AbstractInterceptor 
{
   
   @AroundInvoke
   public Object rollbackIfNecessary(InvocationContext invocation) throws Exception 
   {
      try
      {
         final Object result = invocation.proceed();
         if (invocation.getMethod().isAnnotationPresent(Rollback.class)) 
         {
            String[] outcomes = invocation.getMethod().getAnnotation(Rollback.class).ifOutcome();
            List<String> outcomeList = Arrays.asList(outcomes);
            boolean isRollback = outcomes.length==0 || 
                  ( result==null && outcomeList.contains(Outcome.REDISPLAY) ) || 
                  outcomeList.contains(result);
            if ( isRollback )
            {
               if ( component.getType()==ComponentType.JAVA_BEAN )
               {
                  //For JavaBeans, we assume the UT is accessible
                  Transactions.setUserTransactionRollbackOnly();
               }
               else
               {
                  //For session beans, we have to assume it might be
                  //a CMT, so use the EJBContext
                  Transactions.getEJBContext().setRollbackOnly();
               }
            }
         }
         return result;
      }
      catch (Exception e)
      {
         //Any exception that propagates out of a JavaBean component
         //causes a transaction rollback
         if ( component.getType()==ComponentType.JAVA_BEAN )
         {
            try
            {
               Transactions.setUserTransactionRollbackOnly();
            }
            catch (Exception te) {} //swallow
         }
         throw e;
      }
   }
   
}

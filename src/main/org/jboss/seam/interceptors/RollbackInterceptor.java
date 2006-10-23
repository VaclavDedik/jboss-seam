//$Id$
package org.jboss.seam.interceptors;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import javax.ejb.ApplicationException;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.jboss.seam.ComponentType;
import org.jboss.seam.annotations.Interceptor;
import org.jboss.seam.annotations.Outcome;
import org.jboss.seam.annotations.Rollback;
import org.jboss.seam.util.Transactions;

/**
 * Automatically sets transactions to rollback only.
 * 
 * @author Gavin King
 */
@Interceptor(stateless=true, around=OutcomeInterceptor.class)
public class RollbackInterceptor extends AbstractInterceptor 
{
   
   @AroundInvoke
   public Object rollbackIfNecessary(InvocationContext invocation) throws Exception 
   {
      try
      {
         final Object result = invocation.proceed();
         if ( isRollbackRequired( invocation.getMethod(), result ) )
         {
            if ( getComponent().getType()==ComponentType.JAVA_BEAN )
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
         return result;
      }
      catch (Exception e)
      {
         //Reproduce the EJB3 rollback rules for JavaBean components
         if ( getComponent().getType()==ComponentType.JAVA_BEAN )
         {
            if ( isRollbackRequired(e) )
            {
               try
               {
                  Transactions.setUserTransactionRollbackOnly();
               }
               catch (Exception te) {} //swallow
            }
         }
         throw e;
      }
   }

   private boolean isRollbackRequired(Method method, final Object result)
   {
      if ( !method.isAnnotationPresent(Rollback.class) ) return false;
      String[] outcomes = method.getAnnotation(Rollback.class).ifOutcome();
      List<String> outcomeList = Arrays.asList(outcomes);
      return outcomes.length==0 || 
            ( result==null && outcomeList.contains(Outcome.REDISPLAY) ) || 
            outcomeList.contains(result);
   }

   private boolean isRollbackRequired(Exception e)
   {
      Class<? extends Exception> clazz = e.getClass();
      return ( (e instanceof RuntimeException) && !clazz.isAnnotationPresent(ApplicationException.class) ) || 
            ( clazz.isAnnotationPresent(ApplicationException.class) && clazz.getAnnotation(ApplicationException.class).rollback() );
   }
   
}

//$Id$
package org.jboss.seam.interceptors;

import static org.jboss.seam.ComponentType.JAVA_BEAN;
import static org.jboss.seam.util.EJB.APPLICATION_EXCEPTION;
import static org.jboss.seam.util.EJB.rollback;

import org.jboss.seam.annotations.ApplicationException;
import org.jboss.seam.annotations.AroundInvoke;
import org.jboss.seam.annotations.Interceptor;
import org.jboss.seam.intercept.InvocationContext;
import org.jboss.seam.transaction.Transaction;

/**
 * Automatically sets the current transaction to rollback 
 * only when an exception is thrown.
 * 
 * @author Gavin King
 */
@Interceptor(stateless=true)
public class RollbackInterceptor extends AbstractInterceptor 
{
   private static final long serialVersionUID = 5551801508325093417L;
   
   @AroundInvoke
   public Object aroundInvoke(InvocationContext invocation) throws Exception 
   {
      try
      {
         return invocation.proceed();
      }
      catch (Exception e)
      {
         if ( isRollbackRequired(e) )
         {
            try
            {
               Transaction.instance().setRollbackOnly();
            }
            catch (Exception te) {} //swallow
         }
         throw e;
      }
   }
   
   private boolean isRollbackRequired(Exception e)
   {
      boolean isJavaBean = getComponent().getType()==JAVA_BEAN;
      Class<? extends Exception> clazz = e.getClass();
      return ( isJavaBean && (e instanceof RuntimeException) && !clazz.isAnnotationPresent(APPLICATION_EXCEPTION) && !clazz.isAnnotationPresent(ApplicationException.class) ) || 
            ( isJavaBean && clazz.isAnnotationPresent(APPLICATION_EXCEPTION) && rollback( clazz.getAnnotation(APPLICATION_EXCEPTION) ) ) ||
            ( clazz.isAnnotationPresent(ApplicationException.class) && clazz.getAnnotation(ApplicationException.class).rollback() );
   }
   
}

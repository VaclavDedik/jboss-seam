//$Id$
package org.jboss.seam.transaction;

import static org.jboss.seam.ComponentType.JAVA_BEAN;
import static org.jboss.seam.util.EJB.APPLICATION_EXCEPTION;
import static org.jboss.seam.util.EJB.rollback;

import org.jboss.seam.annotations.ApplicationException;
import org.jboss.seam.annotations.intercept.AroundInvoke;
import org.jboss.seam.annotations.intercept.Interceptor;
import org.jboss.seam.intercept.AbstractInterceptor;
import org.jboss.seam.intercept.InvocationContext;
import org.jboss.seam.util.JSF;

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
      return ( isSystemException(e, isJavaBean, clazz) ) || 
            ( isJavaBean && clazz.isAnnotationPresent(APPLICATION_EXCEPTION) && rollback( clazz.getAnnotation(APPLICATION_EXCEPTION) ) ) ||
            ( clazz.isAnnotationPresent(ApplicationException.class) && clazz.getAnnotation(ApplicationException.class).rollback() );
   }

   private boolean isSystemException(Exception e, boolean isJavaBean, Class<? extends Exception> clazz)
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

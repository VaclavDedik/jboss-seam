package org.jboss.seam.interceptors;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

import org.jboss.seam.annotations.AroundInvoke;
import org.jboss.seam.annotations.Interceptor;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.intercept.InvocationContext;
import org.jboss.seam.util.Work;

/**
 * Implements transaction propagation rules for Seam JavaBean components.
 * 
 * @author Gavin King
 *
 */
@Interceptor(stateless=true,
             around={RollbackInterceptor.class, BusinessProcessInterceptor.class, ConversationInterceptor.class})
public class TransactionInterceptor extends AbstractInterceptor
{
   
   @AroundInvoke
   public Object doInTransactionIfNecessary(final InvocationContext invocation) throws Exception
   {
      return new Work()
      {

         @Override
         protected Object work() throws Exception
         {
            return invocation.proceed();
         }

         @Override
         protected boolean isNewTransactionRequired(boolean transactionActive)
         {
            return isNewTransactionRequired( invocation.getMethod(), getComponent().getBeanClass(), transactionActive );
         }

         private boolean isNewTransactionRequired(Method method, Class beanClass, boolean transactionActive)
         {
            return isTransactionAnnotationPresent(method) ? 
                  isNewTransactionRequired(method, transactionActive) :
                  isTransactionAnnotationPresent(beanClass) && isNewTransactionRequired(beanClass, transactionActive);
         }

         private boolean isTransactionAnnotationPresent(AnnotatedElement element)
         {
            return element.isAnnotationPresent(Transactional.class);
         }

         private boolean isNewTransactionRequired(AnnotatedElement element, boolean transactionActive)
         {
            return element.getAnnotation(Transactional.class).value().isNewTransactionRequired(transactionActive);
         }
         
      }.workInTransaction();      
   }

}

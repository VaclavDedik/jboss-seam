package org.jboss.seam.interceptors;

import java.lang.reflect.AnnotatedElement;

import org.jboss.seam.annotations.AroundInvoke;
import org.jboss.seam.annotations.Interceptor;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.intercept.InvocationContext;
import org.jboss.seam.util.Work;

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
         protected boolean isTransactional()
         {
            return isTransactional( invocation.getMethod() ) || 
                  isTransactional( getComponent().getBeanClass() );
         }

         private boolean isTransactional(AnnotatedElement element)
         {
            return element.isAnnotationPresent(Transactional.class);
         }
         
      }.workInTransaction();      
   }

}

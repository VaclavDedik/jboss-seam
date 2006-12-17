//$Id$
package org.jboss.seam.example.booking;

import java.lang.reflect.Method;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.jboss.seam.Component;
import org.jboss.seam.InterceptorType;
import org.jboss.seam.annotations.Interceptor;

@Interceptor(type=InterceptorType.CLIENT)
public class LoggedInInterceptor
{

   @AroundInvoke
   public Object checkLoggedIn(InvocationContext invocation) throws Exception
   {
      Login login = (Login) Component.getInstance(LoginAction.class);
      if ( login.isLoggedIn() )
      {
         return invocation.proceed();
      }
      else
      {
         Method method = invocation.getMethod();
         Class<?> returnType = method.getReturnType();
         if ( returnType.equals(void.class) || returnType.equals(String.class) )
         {
            return null;
         }
         else
         {
            return method.invoke( invocation.getTarget(), invocation.getParameters() );
         }
      }
   }

}

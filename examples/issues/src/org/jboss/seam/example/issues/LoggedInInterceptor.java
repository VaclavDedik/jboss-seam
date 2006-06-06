//$Id$
package org.jboss.seam.example.issues;

import java.lang.reflect.Method;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.faces.event.PhaseId;

import org.jboss.seam.annotations.Around;
import org.jboss.seam.annotations.Within;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.interceptors.BijectionInterceptor;
import org.jboss.seam.interceptors.BusinessProcessInterceptor;
import org.jboss.seam.interceptors.ConversationInterceptor;
import org.jboss.seam.interceptors.RemoveInterceptor;
import org.jboss.seam.interceptors.ValidationInterceptor;

@Around({BijectionInterceptor.class, ValidationInterceptor.class, 
   ConversationInterceptor.class, BusinessProcessInterceptor.class})
@Within(RemoveInterceptor.class)
public class LoggedInInterceptor
{

   @AroundInvoke
   public Object checkLoggedIn(InvocationContext invocation) throws Exception
   {
      boolean noNeedToLogin = !invocation.getMethod().isAnnotationPresent(LoggedIn.class) ||
            Contexts.getSessionContext().get("loggedIn")!=null;
      if ( Lifecycle.getPhaseId()==PhaseId.INVOKE_APPLICATION )
      {
         if (noNeedToLogin) 
         {
            return invocation.proceed();
         }
         else 
         {
            //Conversation.instance().leave();
            return "login";
         }
      }
      else
      {
         if ( noNeedToLogin )
         {
            return invocation.proceed();
         }
         else
         {
            Method method = invocation.getMethod();
            if ( method.getReturnType().equals(void.class) )
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

}

package com.jboss.dvd.seam;

import java.lang.reflect.Method;

import javax.ejb.AroundInvoke;
import javax.ejb.InvocationContext;
import javax.faces.event.PhaseId;

import org.jboss.logging.Logger;
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
    private static final Logger log = Logger.getLogger(LoggedInInterceptor.class);

    @AroundInvoke
    public Object checkLoggedIn(InvocationContext invocation) 
        throws Exception
    {
       boolean isLoggedIn = Contexts.getSessionContext().get(LoginIfInterceptor.LOGIN_KEY)!=null;
       if ( Lifecycle.getPhaseId()==PhaseId.INVOKE_APPLICATION )
       {
          if (isLoggedIn) 
          {
             log.info("User is already logged in");
             return invocation.proceed();
          }
          else 
          {
             log.info("User is not logged in");
             return "login";
          }
       }
       else
       {
          if ( isLoggedIn )
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
                return method.invoke( invocation.getBean(), invocation.getParameters() );
             }
          }
       }
    }
}

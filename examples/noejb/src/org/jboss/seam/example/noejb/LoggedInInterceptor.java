//$Id$
package org.jboss.seam.example.noejb;

import javax.ejb.AroundInvoke;
import javax.ejb.InvocationContext;
import javax.faces.event.PhaseId;

import org.jboss.logging.Logger;
import org.jboss.seam.annotations.Around;
import org.jboss.seam.annotations.Within;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.interceptors.BijectionInterceptor;
import org.jboss.seam.interceptors.ConversationInterceptor;
import org.jboss.seam.interceptors.RemoveInterceptor;
import org.jboss.seam.interceptors.ValidationInterceptor;

@Around({BijectionInterceptor.class, ValidationInterceptor.class, ConversationInterceptor.class})
@Within(RemoveInterceptor.class)
public class LoggedInInterceptor
{
   private static final Logger log = Logger.getLogger(LoggedInInterceptor.class);

   @AroundInvoke
   public Object checkLoggedIn(InvocationContext invocation) throws Exception
   {
      if ( Lifecycle.getPhaseId()!=PhaseId.INVOKE_APPLICATION )
      {
         return invocation.proceed();
      }

      boolean isLoggedIn = Contexts.getSessionContext().get("loggedIn")!=null;
      if (isLoggedIn) 
      {
         log.debug("User is already logged in");
         return invocation.proceed();
      }
      else 
      {
         log.debug("User is not logged in");
         return "login";
      }
   }

}

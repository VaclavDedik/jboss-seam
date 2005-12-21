//$Id$
package org.jboss.seam.example.bpm;

import javax.ejb.AroundInvoke;
import javax.ejb.InvocationContext;
import javax.faces.event.PhaseId;

import org.jboss.seam.annotations.Around;
import org.jboss.seam.annotations.Within;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.interceptors.BijectionInterceptor;
import org.jboss.seam.interceptors.ConversationInterceptor;
import org.jboss.seam.interceptors.RemoveInterceptor;
import org.jboss.seam.interceptors.ValidationInterceptor;

@Around( { BijectionInterceptor.class, ValidationInterceptor.class, ConversationInterceptor.class } )
@Within( RemoveInterceptor.class )
public class LoggedInInterceptor
{

   @AroundInvoke
   public Object checkLoggedIn(InvocationContext invocation) throws Exception
   {
      if ( Lifecycle.getPhaseId()!=PhaseId.INVOKE_APPLICATION )
      {
         return invocation.proceed();
      }
      
      if ( Contexts.getSessionContext().get("loggedIn")==null )
      {
         return "home";
      }
      else
      {
         return invocation.proceed();
      }
   }

}

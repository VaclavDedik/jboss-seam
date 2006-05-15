//$Id$
package org.jboss.seam.interceptors;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.faces.event.PhaseId;

import org.jboss.seam.annotations.Outcome;
import org.jboss.seam.contexts.Lifecycle;

/**
 * Translates Outcome.RETRY to null for JSF
 * 
 * @author Gavin King
 */
public class OutcomeInterceptor extends AbstractInterceptor
{
   @AroundInvoke
   public Object interceptOutcome(InvocationContext invocation) throws Exception
   {
      final Object result = invocation.proceed();
      return Lifecycle.getPhaseId()==PhaseId.INVOKE_APPLICATION && 
            Outcome.REDISPLAY.equals(result) ? 
                  null : result;
   }
}

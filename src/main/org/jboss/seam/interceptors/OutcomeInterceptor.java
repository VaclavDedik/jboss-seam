//$Id$
package org.jboss.seam.interceptors;

import javax.ejb.AroundInvoke;
import javax.ejb.InvocationContext;

import org.jboss.seam.annotations.Outcome;

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
      return Outcome.REDISPLAY.equals(result) ? null : result;
   }
}

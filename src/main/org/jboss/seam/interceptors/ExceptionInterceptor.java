//$Id$
package org.jboss.seam.interceptors;

import javax.faces.context.FacesContext;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.jboss.seam.InterceptorType;
import org.jboss.seam.annotations.Interceptor;
import org.jboss.seam.core.Exceptions;

/**
 * Handles exceptions annotation @Redirect, @HttpError or @Render.
 * 
 * @author Gavin King
 */
@Interceptor(stateless=true, type=InterceptorType.CLIENT)
public class ExceptionInterceptor extends AbstractInterceptor
{

   @AroundInvoke
   public Object handleExceptions(InvocationContext invocation) throws Exception
   {
      boolean outermost = invocation.getContextData().get("org.jboss.seam.outermostExceptionInterceptor")==null;
      invocation.getContextData().put("org.jboss.seam.outermostExceptionInterceptor", true);
      try
      {
         return invocation.proceed();
      }
      catch (Exception e)
      {
         if ( outermost && FacesContext.getCurrentInstance()!=null )
         {
            return Exceptions.instance().handle(e);
         }
         else
         {
            throw e;
         }
      }
   }


}

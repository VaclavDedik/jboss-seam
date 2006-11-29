// $Id$
package org.jboss.seam.interceptors;

import static org.jboss.seam.contexts.Contexts.getEventContext;
import static org.jboss.seam.contexts.Contexts.isEventContextActive;

import javax.faces.context.FacesContext;

import org.jboss.seam.InterceptorType;
import org.jboss.seam.annotations.AroundInvoke;
import org.jboss.seam.annotations.Interceptor;
import org.jboss.seam.core.Exceptions;
import org.jboss.seam.intercept.InvocationContext;

/**
 * Handles exceptions annotated @Redirect, @HttpError or
 * @Render.
 * 
 * @author Gavin King
 */
@Interceptor(stateless = true, type = InterceptorType.CLIENT)
public class ExceptionInterceptor extends AbstractInterceptor
{

   private static final String OUTERMOST_EXCEPTION_INTERCEPTOR = "org.jboss.seam.outermostExceptionInterceptor";

   @AroundInvoke
   public Object handleExceptions(InvocationContext invocation) throws Exception
   {
      boolean outermost = isEventContextActive() && 
                        getEventContext().get(OUTERMOST_EXCEPTION_INTERCEPTOR) == null;
      if (outermost)
      {
         getEventContext().set(OUTERMOST_EXCEPTION_INTERCEPTOR, true);
      }
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
      finally
      {
         if (outermost) 
         {
            getEventContext().remove(OUTERMOST_EXCEPTION_INTERCEPTOR);
         }
      }
   }
}

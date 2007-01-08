package org.jboss.seam.interceptors;

import java.lang.reflect.Method;

import org.jboss.seam.annotations.AroundInvoke;
import org.jboss.seam.annotations.Interceptor;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.intercept.InvocationContext;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.SeamSecurityManager;

/**
 * Provides authorization services for component invocations.
 * 
 * @author Shane Bryzak
 */
@Interceptor(stateless = true, around = ValidationInterceptor.class, within = BijectionInterceptor.class)
public class SecurityInterceptor extends AbstractInterceptor
{
   private static final long serialVersionUID = -6567750187000766925L;

   @AroundInvoke
   public Object checkSecurity(InvocationContext invocation) throws Exception
   {
      Restrict r = null;

      Method method = invocation.getMethod();

      if (method.isAnnotationPresent(Restrict.class))
         r = method.getAnnotation(Restrict.class);
      else if (method.getDeclaringClass().isAnnotationPresent(Restrict.class))
         r = method.getDeclaringClass().getAnnotation(Restrict.class);

      if (r != null)
      {
         if (!Identity.loggedIn())
            throw new SecurityException("Not logged in");
         
         if (!SeamSecurityManager.instance().evaluateExpression(r.value()))
            throw new SecurityException(String.format(
                  "Authorization check failed for expression [%s]", r.value()));
      }

      return invocation.proceed();
   }
}

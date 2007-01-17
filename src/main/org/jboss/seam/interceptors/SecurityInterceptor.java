package org.jboss.seam.interceptors;

import java.lang.reflect.Method;

import org.jboss.seam.Seam;
import org.jboss.seam.annotations.AroundInvoke;
import org.jboss.seam.annotations.Interceptor;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.intercept.InvocationContext;
import org.jboss.seam.security.AuthorizationException;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.NotLoggedInException;
import org.jboss.seam.security.SeamSecurityManager;

/**
 * Provides authorization services for component invocations.
 * 
 * @author Shane Bryzak
 */
@Interceptor(stateless = true, around = ValidationInterceptor.class, 
         within = {BijectionInterceptor.class, ExceptionInterceptor.class})
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
         String expr = r.value() != null && !"".equals(r.value()) ? r.value() : 
            createDefaultExpr(method);         
         
         Identity.instance().checkRestriction(expr);
      }

      return invocation.proceed();
   }
   
   /**
    * Creates a default security expression for a specified method.  The method must
    * be a method of a Seam component.
    * 
    * @param method The method for which to create a default permission expression 
    * @return The generated security expression.
    */
   private String createDefaultExpr(Method method)
   {
      String name = Seam.getComponentName(method.getDeclaringClass());
      if (name == null)
      {
         throw new IllegalArgumentException(String.format(
                  "Method %s is not a component method", method));
      }
      
      return String.format("#{s:hasPermission('%s','%s')}", name, method.getName());
   }
}

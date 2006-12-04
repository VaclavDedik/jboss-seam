package org.jboss.seam.interceptors;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.annotations.AroundInvoke;
import org.jboss.seam.annotations.Interceptor;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.intercept.InvocationContext;
import org.jboss.seam.security.Identity;

/**
 * Provides authorization services for component invocations.
 *
 * @author Shane Bryzak
 */
@Interceptor(stateless = true,
             around = ValidationInterceptor.class,
             within = BijectionInterceptor.class)
public class SecurityInterceptor extends AbstractInterceptor
{
  private static final Log log = LogFactory.getLog(SecurityInterceptor.class);

  @AroundInvoke
  public Object checkSecurity(InvocationContext invocation)
      throws Exception
  {
    Restrict r = null;

    Method method = invocation.getMethod();

    if (method.isAnnotationPresent(Restrict.class))
      r = method.getAnnotation(Restrict.class);
    else if (method.getDeclaringClass().isAnnotationPresent(Restrict.class))
      r = method.getDeclaringClass().getAnnotation(Restrict.class);

    if (r != null)
    {
      Identity identity = Identity.instance();
      if (!identity.isValid())
        throw new SecurityException("Invalid identity");
      
      /** todo perform restriction check here */
    }

    return invocation.proceed();
  }
}

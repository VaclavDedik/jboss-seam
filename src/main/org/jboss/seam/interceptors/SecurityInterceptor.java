package org.jboss.seam.interceptors;

import java.lang.reflect.Method;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Interceptor;
import org.jboss.seam.annotations.Secure;

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
  public static boolean isComponentSecure(Component component)
  {
    if (component.getBeanClass().isAnnotationPresent(Secure.class))
      return true;
    else
    {
      for (Method m : component.getBeanClass().getDeclaredMethods())
      {
        if (m.isAnnotationPresent(Secure.class))
          return true;
      }
    }

    return false;
  }

  @AroundInvoke
  public Object checkSecurity(InvocationContext invocation)
      throws Exception
  {
    Method method = invocation.getMethod();

    /** @todo Authorize the user before invoking the method.  For now, just go ahead */

    return invocation.proceed();
  }
}

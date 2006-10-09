package org.jboss.seam.interceptors;

import java.lang.reflect.Method;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Interceptor;
import org.jboss.seam.annotations.Secure;
import org.jboss.seam.security.SeamSecurityManager;
import org.jboss.seam.security.Authentication;
import org.jboss.seam.security.AuthenticationException;

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

    Secure sec = null;
    if (method.isAnnotationPresent(Secure.class))
      sec = method.getAnnotation(Secure.class);
    else if (method.getDeclaringClass().isAnnotationPresent(Secure.class))
      sec = method.getDeclaringClass().getAnnotation(Secure.class);

    if (sec != null)
    {
      boolean redirectToLogin = false;
      Authentication auth = null;

      try
      {
        auth = Authentication.instance();
        if (!auth.isValid())
          redirectToLogin = true;
      }
      catch (AuthenticationException ex)
      {
        if (String.class.equals(method.getReturnType()))
          redirectToLogin = true;
        else
          throw ex;
      }

      if (redirectToLogin)
      {
        //          return SeamSecurityManager.instance().getConfiguration().getLoginAction();
                  /** @todo Get this action from the security config */
          return "login";
      }

      // If roles() are specified check them first
      if (sec.roles().length > 0)
      {
        for (String role : sec.roles())
        {
          if (auth.isUserInRole(role))
            return invocation.proceed();
        }
      }

      // No roles matched, check permissions
      if (sec.permissions().length > 0)
      {
//        SeamSecurityManager.instance().checkAcls();
      }

      // Authorization has failed.. redirect the user to an error page
      if (sec.onfail() != null && !"".equals(sec.onfail()))
        return sec.onfail();

      /** @todo Get this action from the security config */
      return "error";
    }

    return invocation.proceed();
  }
}

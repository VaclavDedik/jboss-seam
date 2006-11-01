package org.jboss.seam.interceptors;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.annotations.AroundInvoke;
import org.jboss.seam.annotations.Interceptor;
import org.jboss.seam.annotations.Permission;
import org.jboss.seam.annotations.Secure;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.intercept.InvocationContext;
import org.jboss.seam.security.Authentication;
import org.jboss.seam.security.AuthenticationException;
import org.jboss.seam.security.SeamSecurityManager;

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
    Secure sec = null;

    try
    {
      Method method = invocation.getMethod();

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
          return SeamSecurityManager.instance().getLoginAction();
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

        // No roles match, check permissions
        try
        {
          if (sec.permissions().length > 0)
          {
            for (Permission p : sec.permissions())
            {
              SeamSecurityManager.instance().checkPermission(p.name(), p.action());
            }
          }
        }
        catch (SecurityException ex)
        {
          log.info(ex.getMessage());
          FacesMessages.instance().add(ex.getMessage());
          // Fall through to error page
        }

        return forwardToErrorPage(sec);
      }

      return invocation.proceed();
    }
    catch (SecurityException ex)
    {
      return forwardToErrorPage(sec);
    }
  }

  private String forwardToErrorPage(Secure sec)
  {
    // Authorization has failed.. redirect the user to an error page
    if (sec != null && sec.onfail() != null && !"".equals(sec.onfail()))
      return sec.onfail();

    return SeamSecurityManager.instance().getSecurityErrorAction();
  }
}

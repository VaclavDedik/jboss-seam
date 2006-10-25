package org.jboss.seam.security.adapter.jboss;

import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.jboss.seam.security.Authentication;
import org.jboss.seam.security.adapter.AuthenticationAdapter;
import org.jboss.security.auth.callback.UsernamePasswordHandler;

/**
 * @author Shane Bryzak
 */
public class JBossAuthenticationAdapter implements AuthenticationAdapter
{
  private static ThreadLocal<LoginContext> contexts = new ThreadLocal<LoginContext>();

  public void beginRequest()
  {
    Authentication auth = Authentication.instance();

    CallbackHandler handler = new UsernamePasswordHandler(
        auth.getPrincipal().toString(),
        auth.getCredentials());
    try
    {
      contexts.set(new LoginContext("client-login", handler));
      contexts.get().login();
    }
    catch (LoginException ex)
    {
      ex.printStackTrace();
    }
  }

  public void endRequest()
  {
    try
    {
      contexts.get().logout();
    }
    catch (LoginException ex)
    {
    }
    finally
    {
      contexts.remove();
    }
  }
}

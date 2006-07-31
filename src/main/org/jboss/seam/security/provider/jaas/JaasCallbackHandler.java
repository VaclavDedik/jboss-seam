package org.jboss.seam.security.provider.jaas;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;

/**
 * Handles JAAS authentication callbacks.
 *
 * @author Shane Bryzak
 */
public class JaasCallbackHandler implements CallbackHandler
{
  private String username;
  private String password;

  public JaasCallbackHandler(String username, String password)
  {
    this.username = username;
    this.password = password;
  }

  public void handle(Callback[] callback)
  {
    for (Callback cb : callback)
    {
      if (cb instanceof NameCallback)
      {
        ((NameCallback) cb).setName(username);
      }
      else if (cb instanceof PasswordCallback)
      {
        ((PasswordCallback) cb).setPassword(password.toCharArray());
      }
      else
        /** @todo  */
        System.out.println("Unknown callback: " + cb);
    }
  }
}

package org.jboss.seam.security.realm;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

/**
 * JAAS realm implementation
 *
 * @author Shane Bryzak
 */
public class JaasRealm implements Realm
{
  public Principal authenticate(String username, String credentials)
  {
    try
    {
      /** @todo get the JAAS configuration name from the config file? */
      LoginContext loginContext = new LoginContext("securityexample",
          new JaasCallbackHandler(username, credentials));

      loginContext.login();

      return createPrincipal(username, loginContext.getSubject());
    }
    catch (LoginException ex)
    {
      return null;
    }
  }

  public Principal authenticate(String username, byte[] credentials)
  {
    return null;
  }

  protected Principal createPrincipal(String username, Subject subject)
  {
    List<String> roles = new ArrayList<String>();
    Principal userPrincipal = null;

    for (Principal principal : subject.getPrincipals())
    {
      /** @todo since JAAS doesn't tell us which principals are the user and
       * which are roles, we need to work it out ourselves */
    }

    // Return the resulting Principal for our authenticated user
    return new SeamPrincipal(this, username, roles, userPrincipal);
  }
}

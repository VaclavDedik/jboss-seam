package org.jboss.seam.security.provider.jaas;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

/**
 * JAAS authentication provider
 *
 * @author Shane Bryzak
 */
public class JaasAuthenticationProvider //implements AuthenticationProvider
{
  public Principal authenticate(String username, String credentials)
  {
    try
    {
      /** @todo This is a hack just to get things working.  This stuff should be
       * loaded from the config file */
      Map<String,?> options = new HashMap<String,Object>();
      final AppConfigurationEntry entry = new AppConfigurationEntry(
          "org.jboss.seam.security.loginmodule.SeamLoginModule",
          LoginModuleControlFlag.REQUIRED, options);
      Configuration config = new Configuration() {
        public AppConfigurationEntry[] getAppConfigurationEntry(String name)
        {
          return new AppConfigurationEntry[] {entry};
        }
        public void refresh() {}
      };

      /** @todo get the JAAS configuration name from the config file? */
      LoginContext loginContext = new LoginContext("seam", new Subject(),
          new JaasCallbackHandler(username, credentials), config);

      loginContext.login();

      return createPrincipal(username, loginContext.getSubject());
    }
    catch (LoginException ex)
    {
      throw new SecurityException("Authentication failed", ex);
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
//      if (principal instanceof UserPrincipal && userPrincipal == null)
//      {
//        userPrincipal = principal;
//      }
//      else if (principal instanceof RolePrincipal)
//      {
//        roles.add(principal.getName());
//      }
    }

    // Return the resulting Principal for our authenticated user
//    return new SeamPrincipal(null, username, roles, userPrincipal);
    return null;
  }
}

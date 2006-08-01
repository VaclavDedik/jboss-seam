package org.jboss.seam.security.authenticator;

import java.util.ArrayList;
import java.util.List;

import static org.jboss.seam.ScopeType.APPLICATION;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.security.Authentication;
import org.jboss.seam.security.AuthenticationException;
import org.jboss.seam.security.provider.AuthenticationProvider;

/**
 *
 * @author Shane Bryzak
 */
@Name("org.jboss.seam.security.Authenticator")
@Scope(APPLICATION)
@Startup
public class ProviderAuthenticator implements Authenticator
{
  private List<AuthenticationProvider> providers = new ArrayList<AuthenticationProvider>();

  public Authentication authenticate(Authentication authentication)
      throws AuthenticationException
  {
    for (AuthenticationProvider provider : providers)
    {
      Authentication result = provider.authenticate(authentication);
      if (result != null)
        return result;
    }

    throw new AuthenticationException("Provider not found");
  }

  public void setProviders(List<String> providerNames)
  {
    for (String providerName : providerNames)
    {
      Object provider = null;
      try
      {
        provider = Class.forName(providerName).newInstance();
        providers.add((AuthenticationProvider) provider);
      }
      catch (Exception ex)
      {
//        log.error("Error creating provider", ex);
      }
    }
  }
}

package org.jboss.seam.security.authenticator;

import java.util.ArrayList;
import java.util.List;

import static org.jboss.seam.ScopeType.APPLICATION;
import org.jboss.seam.Component;
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
  /**
   *
   */
  private List<Object> providers = new ArrayList<Object>();

  /**
   *
   * @param authentication Authentication
   * @return Authentication
   * @throws AuthenticationException
   */
  public Authentication authenticate(Authentication authentication)
      throws AuthenticationException
  {
    for (Object p : providers)
    {
      AuthenticationProvider provider = null;

      if (p instanceof AuthenticationProvider)
        provider = (AuthenticationProvider) p;
      else if (p instanceof Component)
        provider = (AuthenticationProvider) ((Component) p).newInstance();

      Authentication result = provider.authenticate(authentication);
      if (result != null)
        return result;
    }

    throw new AuthenticationException("Provider not found");
  }

  /**
   *
   * @param authentication Authentication
   */
  public void unauthenticate(Authentication authentication)
  {
    for (Object p : providers)
    {
      AuthenticationProvider provider = null;

      if (p instanceof AuthenticationProvider)
        provider = (AuthenticationProvider) p;
      else if (p instanceof Component)
        provider = (AuthenticationProvider) ((Component) p).newInstance();

      provider.unauthenticate(authentication);
    }
  }

  /**
   *
   * @param providerNames List
   */
  public void setProviders(List<String> providerNames)
  {
    for (String providerName : providerNames)
    {
      Object provider = null;
      try
      {
        Component comp = Component.forName(providerName);
        if (comp != null)
          providers.add(comp);
        else
        {
          provider = Class.forName(providerName).newInstance();
          providers.add( (AuthenticationProvider) provider);
        }
      }
      catch (Exception ex)
      {
//        log.error("Error creating provider", ex);
      }
    }
  }
}

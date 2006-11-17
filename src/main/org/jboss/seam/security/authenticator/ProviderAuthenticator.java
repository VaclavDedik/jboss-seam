package org.jboss.seam.security.authenticator;

import java.util.ArrayList;
import java.util.List;

import static org.jboss.seam.ScopeType.APPLICATION;
import org.jboss.seam.Component;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.security.Authentication;
import org.jboss.seam.security.AuthenticationException;
import org.jboss.seam.security.provider.AuthenticationProvider;
import org.jboss.seam.util.Reflections;

/**
 * Performs authentication services against one or more providers.
 *
 * @author Shane Bryzak
 */
@Name("org.jboss.seam.security.authenticator")
@Scope(APPLICATION)
public class ProviderAuthenticator extends Authenticator
{
  private List<Object> providers = new ArrayList<Object> ();

  /**
   *
   * @param authentication Authentication
   * @return Authentication
   * @throws AuthenticationException
   */
  @Override
  public Authentication doAuthentication(Authentication authentication)
      throws AuthenticationException
  {
    for (Object p : providers)
    {
      AuthenticationProvider provider = null;

      if (p instanceof AuthenticationProvider)
        provider = (AuthenticationProvider) p;
      else if (p instanceof Component)
        provider = (AuthenticationProvider) ( (Component) p).newInstance();

      Authentication result = provider.authenticate(authentication);
      if (result != null)
        return result;
    }

    throw new AuthenticationException("Provider not found");
  }

  public void setProviders(Object values)
  {
    if (values instanceof AuthenticationProvider)
    {
      providers.add(values);
    }
    else
    {
      for (Object provider : (List) values)
      {
        if (provider instanceof Component)
          providers.add(provider);
        else
        {
          try
          {
            provider = Reflections.classForName(provider.toString()).newInstance();
            providers.add(provider);
          }
          catch (Exception ex)
          {
//        log.error("Error creating provider", ex);
          }
        }
      }
    }
  }
}

package org.jboss.seam.security.authenticator;

import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.Authentication;
import org.jboss.seam.security.AuthenticationException;
import org.jboss.seam.security.UsernamePasswordToken;
import org.jboss.seam.security.adapter.AuthenticationAdapter;
import org.jboss.seam.util.Reflections;

/**
 *
 *
 * @author Shane Bryzak
 */
@Name("org.jboss.seam.security.authenticator")
@Install(value=false, precedence=BUILT_IN)
public abstract class Authenticator
{
  private List<AuthenticationAdapter> adapters = new ArrayList<AuthenticationAdapter>();

  public static Authenticator instance()
  {
    if (!Contexts.isApplicationContextActive())
       throw new IllegalStateException("No active application context");

    Authenticator instance = (Authenticator) Component.getInstance(
        Authenticator.class, ScopeType.APPLICATION);

    if (instance==null)
    {
      throw new IllegalStateException(
          "No Authenticator could be created, make sure the Component exists in application scope");
    }

    return instance;
  }

  public Authentication authenticate(String username, String password)
      throws AuthenticationException
  {
    return authenticate(new UsernamePasswordToken(username, password));
  }

  public final Authentication authenticate(Authentication authentication)
      throws AuthenticationException
  {
    Authentication auth = doAuthentication(authentication);
    Contexts.getSessionContext().set(Seam.getComponentName(Authentication.class), auth);
    return auth;
  }

  public abstract Authentication doAuthentication(Authentication authentication)
      throws AuthenticationException;

  public void unauthenticateSession()
  {
    Authentication.instance().invalidate();
  }

  public void setAdapters(List<String> adapterNames)
  {
    for (String name : adapterNames)
    {
      try
      {
        adapters.add((AuthenticationAdapter) Reflections.classForName(name).newInstance());
      }
      catch (Exception ex)
      {
      }
    }
  }

  public void beginRequest()
  {
    for (AuthenticationAdapter adapter : adapters)
    {
      adapter.beginRequest();
    }
  }

  public void endRequest()
  {
    for (AuthenticationAdapter adapter : adapters)
    {
      adapter.endRequest();
    }

    if (!Authentication.instance().isValid())
      Contexts.getSessionContext().remove(Seam.getComponentName(Authentication.class));
  }
}

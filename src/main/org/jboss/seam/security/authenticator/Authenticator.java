package org.jboss.seam.security.authenticator;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
import static org.jboss.seam.annotations.Install.BUILT_IN;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.AuthenticationException;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.UsernamePasswordToken;
import org.jboss.seam.security.adapter.AuthenticationAdapter;
import org.jboss.seam.util.Reflections;

/**
 *
 *
 * @author Shane Bryzak
 */
@Name("org.jboss.seam.security.authenticator")
@Install(value=false, precedence=BUILT_IN, dependencies = "org.jboss.seam.securityManager")
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

  public Identity authenticate(String username, String password)
      throws AuthenticationException
  {
    return authenticate(new UsernamePasswordToken(username, password));
  }

  public final Identity authenticate(Identity ident)
      throws AuthenticationException
  {
    Identity auth = doAuthentication(ident);
    Contexts.getSessionContext().set(Seam.getComponentName(Identity.class), auth);
    return auth;
  }

  public abstract Identity doAuthentication(Identity ident)
      throws AuthenticationException;

  public void unauthenticateSession()
  {
    Identity.instance().invalidate();
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

    if (!Identity.instance().isValid())
      Contexts.getSessionContext().remove(Seam.getComponentName(Identity.class));
  }
}

package org.jboss.seam.security;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import static org.jboss.seam.ScopeType.APPLICATION;
import org.jboss.seam.annotations.Startup;

/**
 *
 *
 * @author Shane Bryzak
 */
@Name("org.jboss.seam.security.AuthenticationContext")
@Scope(APPLICATION)
@Startup
public class ThreadLocalAuthenticationContext implements AuthenticationContext
{
  private static ThreadLocal<Authentication> threadLocalContext = new ThreadLocal<Authentication>();

  public Authentication getAuthentication()
  {
    return threadLocalContext.get();
  }

  public void setAuthentication(Authentication authentication)
  {
    threadLocalContext.set(authentication);
  }
}

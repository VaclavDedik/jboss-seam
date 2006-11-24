package org.jboss.seam.remoting;

import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import org.jboss.seam.Component;
import org.jboss.seam.InterceptionType;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;

/**
 * Various configuration options for Seam Remoting
 *
 * @author Shane Bryzak
 */
@Name("org.jboss.seam.remoting.remotingConfig")
@Scope(APPLICATION)
@Install(precedence=BUILT_IN)
@Intercept(InterceptionType.NEVER)
public class RemotingConfig
{
  public static final int DEFAULT_POLL_TIMEOUT = 10; // 10 seconds
  public static final int DEFAULT_POLL_INTERVAL = 1; // 1 second

  private int pollTimeout;
  private int pollInterval;
  private boolean debug;

  public static RemotingConfig instance()
  {
    if (!Contexts.isApplicationContextActive())
       throw new IllegalStateException("No active application context");

    RemotingConfig instance = (RemotingConfig) Component.getInstance(
        RemotingConfig.class, ScopeType.APPLICATION);

    if (instance==null)
    {
      throw new IllegalStateException(
          "No RemotingConfig could be created, make sure the Component exists in application scope");
    }

    return instance;
  }

  public RemotingConfig()
  {
    pollTimeout = DEFAULT_POLL_TIMEOUT;
    pollInterval = DEFAULT_POLL_INTERVAL;
    debug = false;
  }

  public int getPollTimeout()
  {
    return pollTimeout;
  }

  public void setPollTimeout(int pollTimeout)
  {
    this.pollTimeout = pollTimeout;
  }

  public int getPollInterval()
  {
    return pollInterval;
  }

  public void setPollInterval(int pollInterval)
  {
    this.pollInterval = pollInterval;
  }

  public boolean getDebug()
  {
    return debug;
  }

  public void setDebug(boolean debug)
  {
    this.debug = debug;
  }
}

package org.jboss.seam.security.config;

import java.util.Collections;
import java.util.Set;
import org.jboss.seam.contexts.Context;

/**
 * Security Configuration interface.
 *
 * @author Shane Bryzak
 */
public final class SecurityConfig
{
  /**
   * Singleton instance.
   */
  private static final SecurityConfig instance = new SecurityConfig();

  /**
   * Flag indicating whether the configuration has been loaded
   */
  private boolean configLoaded = false;

  /**
   * Security constraints
   */
  private Set<SecurityConstraint> securityConstraints;

  /**
   * The authentication method
   */
  private AuthMethod authMethod;

  /**
   * Security roles with access to the application
   */
  private Set<String> securityRoles;

  /**
   * The ApplicationContext for this application.  This is required by various
   * modules of the security framework.
   */
  private Context applicationContext;

  /**
   * Private constructor.
   */
  private SecurityConfig() {  }

  /**
   * Returns the SecurityConfig singleton
   *
   * @return SecurityConfig
   */
  public static SecurityConfig instance()
  {
    return instance;
  }

  /**
   * Loads the configuration from the specified SecurityConfigLoader
   *
   * @param configLoader SecurityConfigLoader
   */
  public void loadConfig(SecurityConfigLoader configLoader)
      throws SecurityConfigException
  {
    if (configLoaded)
      throw new SecurityConfigException("Configuration already loaded!");
    else
    {
      securityConstraints = configLoader.getSecurityConstraints();
      authMethod = configLoader.getAuthMethod();
      securityRoles = configLoader.getSecurityRoles();
      configLoaded = true;
    }
  }

  /**
   *
   * @param servletContext ServletContext
   */
  public void setApplicationContext(Context ctx)
  {
    this.applicationContext = ctx;
  }

  /**
   *
   * @return ServletContext
   */
  public Context getApplicationContext()
  {
    return applicationContext;
  }

  /**
   *
   * @return Set
   */
  public Set<SecurityConstraint> getSecurityConstraints()
  {
    return Collections.unmodifiableSet(securityConstraints);
  }

  /**
   *
   * @return AuthMethod
   */
  public AuthMethod getAuthMethod()
  {
    return authMethod;
  }

  /**
   *
   * @return Set
   */
  public Set<String> getSecurityRoles()
  {
    return securityRoles;
  }
}

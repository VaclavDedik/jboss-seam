package org.jboss.seam.security.config;

/**
 * Security configuration exception, thrown when there is an error in the
 * security configuration file.
 *
 * @author Shane Bryzak
 */
public class SecurityConfigException extends Exception
{
  public SecurityConfigException(String message)
  {
    super(message);
  }

  public SecurityConfigException(String message, Throwable cause)
  {
    super(message, cause);
  }
}

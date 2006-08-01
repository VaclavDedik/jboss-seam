package org.jboss.seam.security;

/**
 * <p> </p>
 *
 * @author Shane Bryzak
 */
public class AuthenticationException extends Exception
{
  public AuthenticationException()
  {
    super();
  }

  public AuthenticationException(String message)
  {
    super(message);
  }

  public AuthenticationException(String message, Throwable cause)
  {
    super(message, cause);
  }
}

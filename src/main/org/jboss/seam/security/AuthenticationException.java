package org.jboss.seam.security;

import javax.ejb.ApplicationException;

/**
 * @author Shane Bryzak
 */
@ApplicationException
public class AuthenticationException extends RuntimeException
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

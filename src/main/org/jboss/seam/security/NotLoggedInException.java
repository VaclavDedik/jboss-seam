package org.jboss.seam.security;

import javax.ejb.ApplicationException;

/**
 * Thrown when an unauthenticated user attempts to execute a restricted action. 
 * 
 * @author Shane Bryzak
 */
@ApplicationException
public class NotLoggedInException extends RuntimeException
{  
  public NotLoggedInException() 
  {
     super();
  }
  
  public NotLoggedInException(String message)
  {
     super(message);
  }
}

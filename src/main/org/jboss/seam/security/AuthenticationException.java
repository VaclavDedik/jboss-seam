package org.jboss.seam.security;

import javax.ejb.ApplicationException;

/**
 * Thrown by the
 * 
 * @author Shane Bryzak
 */
@ApplicationException
public class AuthenticationException extends RuntimeException
{
   private static final long serialVersionUID = -4150337120205930755L;

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

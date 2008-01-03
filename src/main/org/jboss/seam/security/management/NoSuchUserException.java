package org.jboss.seam.security.management;

/**
 * Thrown when an operation is attempted on a non-existant user.  
 * 
 * @author Shane Bryzak
 */
public class NoSuchUserException extends Exception
{
   public NoSuchUserException(String message)
   {
      super(message);
   }
   
   public NoSuchUserException(String message, Throwable cause)
   {
      super(message, cause);
   }
}

package org.jboss.seam.security.management;

/**
 * Thrown when an exception is encountered during account creation. 
 *  
 * @author Shane Bryzak
 */
public class CreateAccountException extends RuntimeException
{
   public CreateAccountException(String message)
   {
      super(message);
   }
   
   public CreateAccountException(String message, Throwable cause)
   {
      super(message, cause);
   }
}

package org.jboss.seam.security.config;

/**
 * Security configuration exception, thrown when there is an error in the
 * security configuration file.
 * 
 * @author Shane Bryzak
 */
public class SecurityConfigException extends Exception
{
   private static final long serialVersionUID = -5703807591449246104L;

   public SecurityConfigException(String message)
   {
      super(message);
   }

   public SecurityConfigException(String message, Throwable cause)
   {
      super(message, cause);
   }
}

package org.jboss.seam.security.acl;

/**
 * Thrown while generating identities for domain objects.
 * 
 * @author Shane Bryzak
 */
public class IdentityException extends RuntimeException
{
   private static final long serialVersionUID = 3095635738822653218L;

   public IdentityException(String message)
   {
      super(message);
   }

   public IdentityException(String message, Throwable cause)
   {
      super(message, cause);
   }
}

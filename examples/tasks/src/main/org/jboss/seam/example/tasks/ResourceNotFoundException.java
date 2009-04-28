package org.jboss.seam.example.tasks;

/**
 * This exception is mapped to 404 HTTP response code.
 * @author Jozef Hartinger
 *
 */
public class ResourceNotFoundException extends RuntimeException
{

   public ResourceNotFoundException()
   {
   }

   public ResourceNotFoundException(String message)
   {
      super(message);
   }

}

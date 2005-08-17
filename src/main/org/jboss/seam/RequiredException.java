//$Id$
package org.jboss.seam;

public class RequiredException extends RuntimeException
{

   /** The serialVersionUID */
   private static final long serialVersionUID = -5437284703511833879L;

   public RequiredException()
   {
      super();
   }

   public RequiredException(String message)
   {
      super(message);
   }

   public RequiredException(String message, Throwable cause)
   {
      super(message, cause);
   }

   public RequiredException(Throwable cause)
   {
      super(cause);
   }

}

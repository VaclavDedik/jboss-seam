//$Id$
package org.jboss.seam;

public class InstantiationException extends RuntimeException
{

   /** The serialVersionUID */
   private static final long serialVersionUID = -5437284703511833879L;

   public InstantiationException()
   {
      super();
   }

   public InstantiationException(String message)
   {
      super(message);
   }

   public InstantiationException(String message, Throwable cause)
   {
      super(message, cause);
   }

   public InstantiationException(Throwable cause)
   {
      super(cause);
   }

}

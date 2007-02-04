package org.jboss.seam.core;

import java.io.IOException;

public class RedirectException extends RuntimeException
{

   public RedirectException(IOException ioe)
   {
      super(ioe);
   }
   
}

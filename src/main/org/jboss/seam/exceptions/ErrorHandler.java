package org.jboss.seam.exceptions;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Interpolator;

public abstract class ErrorHandler extends ExceptionHandler
{

   protected abstract int getCode(Exception e);
   protected abstract String getMessage(Exception e);
   protected abstract boolean isEnd(Exception e);

   @Override
   public void handle(Exception e) throws Exception
   {
      if ( Contexts.isConversationContextActive() && isEnd(e) ) 
      {
         Conversation.instance().end();
      }
      
      String message = Interpolator.instance().interpolate( getDisplayMessage( e, getMessage(e) ) );
      error( getCode(e), message );
   }

   @Override
   public String toString()
   {
      return "ErrorHandler";
   }
}
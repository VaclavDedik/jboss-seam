package org.jboss.seam.exceptions;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.RedirectException;

public abstract class RedirectHandler extends ExceptionHandler
{
   
   protected abstract String getViewId(Exception e);

   @Override
   public void handle(Exception e) throws Exception
   {
      addFacesMessage( e, getMessage(e) );
      
      if ( Contexts.isConversationContextActive() && isEnd(e) ) 
      {
         Conversation.instance().end();
      }
      try
      {
         redirect( getViewId(e), null );
      }
      catch (RedirectException re)
      {
         //do nothing
      }
   }
}
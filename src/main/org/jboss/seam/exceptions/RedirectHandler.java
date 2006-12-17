package org.jboss.seam.exceptions;

import org.jboss.seam.core.Conversation;
import org.jboss.seam.util.Transactions;

public abstract class RedirectHandler extends ExceptionHandler
{
   
   @Override
   public Object handle(Exception e) throws Exception
   {
      addFacesMessage( e, getMessage(e) );
      if ( isEnd(e) ) Conversation.instance().end();
      if ( isRollback(e) ) Transactions.setTransactionRollbackOnly();
      redirect( getViewId(e), null );
      return rethrow(e);
   }

}
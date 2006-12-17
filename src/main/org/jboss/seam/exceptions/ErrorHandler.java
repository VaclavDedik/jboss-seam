package org.jboss.seam.exceptions;

import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Interpolator;
import org.jboss.seam.util.Transactions;

public abstract class ErrorHandler extends ExceptionHandler
{

   @Override
   public Object handle(Exception e) throws Exception
   {
      if ( isEnd(e) ) Conversation.instance().end();
      if ( isRollback(e) ) Transactions.setTransactionRollbackOnly();
      String message = getMessage(e);
      //addFacesMessage(e, message);
      error( getCode(e), Interpolator.instance().interpolate( getDisplayMessage(e, message) ) );
      return rethrow(e);
   }

}
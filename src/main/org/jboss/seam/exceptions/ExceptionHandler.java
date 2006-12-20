/**
 * 
 */
package org.jboss.seam.exceptions;

import javax.faces.context.FacesContext;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.core.Navigator;
import org.jboss.seam.util.Strings;

public abstract class ExceptionHandler extends Navigator
{
   public abstract Object handle(Exception e) throws Exception;
   public abstract boolean isHandler(Exception e);
   
   protected String getMessage(Exception e)
   {
      throw new UnsupportedOperationException();
   }
   protected String getViewId(Exception e)
   {
      throw new UnsupportedOperationException();
   }
   protected boolean isEnd(Exception e)
   {
      throw new UnsupportedOperationException();
   }
   protected boolean isRollback(Exception e)
   {
      throw new UnsupportedOperationException();
   }
   protected int getCode(Exception e)
   {
      throw new UnsupportedOperationException();
   }

   public static String getDisplayMessage(Exception e, String message)
   {
      if ( Strings.isEmpty(message) && e.getMessage()!=null ) 
      {
         return e.getMessage();
      }
      else
      {
         return message;
      }
   }
   
   public static void addFacesMessage(Exception e, String message)
   {
      if ( Contexts.isConversationContextActive() )
      {
         message = getDisplayMessage(e, message);
         if ( !Strings.isEmpty(message) )
         {
            FacesMessages.instance().add(message);
         }
      }
   }
   
   public static Object rethrow(Exception e) throws Exception
   {
      //SeamExceptionFilter does *not* do these things, which 
      //would normally happen in the phase listener after a 
      //responseComplete() call, but because we are rethrowing,
      //the phase listener might not get called (due to a bug!)
      /*FacesMessages.afterPhase();
      if ( Contexts.isConversationContextActive() )
      {
         Manager.instance().endRequest( ContextAdaptor.getSession( externalContext, true ) );
      }*/
      
      FacesContext facesContext = FacesContext.getCurrentInstance();
      facesContext.responseComplete();
      facesContext.getExternalContext().getRequestMap().put("org.jboss.seam.exceptionHandled", e);
      throw e;
   }

}
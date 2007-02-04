/**
 * 
 */
package org.jboss.seam.exceptions;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.core.Navigator;
import org.jboss.seam.util.Strings;

public abstract class ExceptionHandler extends Navigator
{
   public abstract void handle(Exception e) throws Exception;
   public abstract boolean isHandler(Exception e);
   
   protected String getMessage(Exception e)
   {
      throw new UnsupportedOperationException();
   }
   
   protected boolean isEnd(Exception e)
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
   

}
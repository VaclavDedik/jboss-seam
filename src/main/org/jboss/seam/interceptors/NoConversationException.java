package org.jboss.seam.interceptors;

import javax.ejb.ApplicationException;

@ApplicationException(rollback=true)
public class NoConversationException extends RuntimeException
{
   private String viewId;

   public NoConversationException(String viewId)
   {
      this.viewId = viewId;
   }

   public String getViewId()
   {
      return viewId;
   }

}

package org.jboss.seam.exceptions;

import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Pages;
import org.jboss.seam.core.RedirectException;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

public abstract class RedirectHandler extends ExceptionHandler
{

   private static final LogProvider log = Logging.getLogProvider(RedirectHandler.class);

   protected abstract String getViewId(Exception e);
   protected abstract String getMessage(Exception e);
   protected abstract boolean isEnd(Exception e);
   protected abstract Severity getMessageSeverity(Exception e);

   @Override
   public void handle(Exception e) throws Exception
   {
      String viewId = getViewId(e);
      if (viewId==null)
      {
         //we want to perform a redirect straight back to the current page
         //there is no ViewRoot available, so lets do it the hard way
         String servletPath = ( (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest() ).getServletPath();
         viewId = servletPath.substring(0, servletPath.lastIndexOf('.')) + Pages.getSuffix();
      }
      
      addFacesMessage( getDisplayMessage(e, getMessage(e)), getMessageSeverity(e), e );
      
      if ( Contexts.isConversationContextActive() && isEnd(e) ) 
      {
         Conversation.instance().end();
      }
      
      try
      {
         redirect(viewId, null);
      }
      catch (RedirectException re)
      {
         //do nothing
         log.debug("could not redirect", re);
      }
   }

   @Override
   public String toString()
   {
      return "RedirectHandler";
   }
}
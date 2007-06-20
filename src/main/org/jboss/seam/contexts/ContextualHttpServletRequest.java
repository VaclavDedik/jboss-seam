package org.jboss.seam.contexts;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.jboss.seam.core.ConversationPropagation;
import org.jboss.seam.core.Manager;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.servlet.ServletRequestSessionMap;
import org.jboss.seam.web.ServletContexts;

public abstract class ContextualHttpServletRequest
{
   private static final LogProvider log = Logging.getLogProvider(ContextualHttpServletRequest.class);

   private final HttpServletRequest request;
   
   public ContextualHttpServletRequest(HttpServletRequest request)
   {
      this.request = request;
   }
   
   public abstract void process() throws Exception;
   
   public void run() throws ServletException, IOException
   {
      log.debug("beginning request");
      ServletLifecycle.beginRequest(request);
      ServletContexts.instance().setRequest(request);
      restoreConversationId();
      Manager.instance().restoreConversation();
      ServletLifecycle.resumeConversation(request);
      handleConversationPropagation();
      try
      {
         process();
         //TODO: conversation timeout
         Manager.instance().endRequest( new ServletRequestSessionMap(request)  );
         ServletLifecycle.endRequest(request);
      }
      catch (IOException ioe)
      {
         Lifecycle.endRequest();
         log.error("ended request due to exception", ioe);
         throw ioe;
      }
      catch (ServletException se)
      {
         Lifecycle.endRequest();
         log.error("ended request due to exception", se);
         throw se;
      }
      catch (Exception e)
      {
         Lifecycle.endRequest();
         log.error("ended request due to exception", e);
         throw new ServletException(e);
      }
      finally
      {
         log.debug("ended request");
      }
   }

   protected void handleConversationPropagation()
   {
      Manager.instance().handleConversationPropagation( request.getParameterMap() );
   }

   protected void restoreConversationId()
   {
      ConversationPropagation.instance().restoreConversationId( request.getParameterMap() );
   }
   
}

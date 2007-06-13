package org.jboss.seam.contexts;

import java.io.IOException;

import javax.faces.event.PhaseId;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.jboss.seam.core.ConversationPropagation;
import org.jboss.seam.core.Manager;
import org.jboss.seam.core.ServletContexts;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.servlet.ServletRequestSessionMap;

public abstract class ContextualHttpServletRequest
{
   private static final LogProvider log = Logging.getLogProvider(ContextualHttpServletRequest.class);

   private final HttpServletRequest request;
   private final ServletContext servletContext;
   
   public ContextualHttpServletRequest(HttpServletRequest request, ServletContext servletContext)
   {
      this.request = request;
      this.servletContext = servletContext;
   }
   
   public abstract void process() throws Exception;
   
   public void run() throws ServletException, IOException
   {
      log.debug("beginning request");
      Lifecycle.setPhaseId(PhaseId.INVOKE_APPLICATION);
      Lifecycle.beginRequest(servletContext, request);
      ServletContexts.instance().setRequest(request);
      restoreConversationId();
      Manager.instance().restoreConversation();
      Lifecycle.resumeConversation(request);
      handleConversationPropagation();
      try
      {
         process();
         //TODO: conversation timeout
         Manager.instance().endRequest( new ServletRequestSessionMap(request)  );
         Lifecycle.endRequest(request);
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
         Lifecycle.setPhaseId(null);
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

package org.jboss.seam.servlet;

import java.io.IOException;

import javax.faces.event.PhaseId;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jboss.logging.Logger;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.Manager;

public class SeamServletFilter implements Filter {
   
   private static Logger log = Logger.getLogger(SeamServletFilter.class);
   private ServletContext servletContext;

   public void destroy() {}

   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
      log.debug("beginning request");
      
      HttpSession session = ( (HttpServletRequest) request ).getSession(true);
      Lifecycle.setPhaseId(PhaseId.INVOKE_APPLICATION);
      Lifecycle.setServletRequest(request);
      Lifecycle.beginRequest(servletContext, session);
      String conversationId = Manager.instance().restore( null, request.getParameterMap() );
      Lifecycle.resumeConversation( session, conversationId );
      try
      {
         chain.doFilter(request, response);
         //TODO: conversation timeout
         Manager.instance().store( (HttpServletResponse) response );
         Lifecycle.endRequest(session);
      }
      catch (Exception e)
      {
         Lifecycle.endRequest();
         log.error("ended request due to exception", e);
         throw new ServletException(e);
      }
      finally
      {
         Lifecycle.setServletRequest(null);
         Lifecycle.setPhaseId(null);
         log.debug("ended request");
      }
   }

   public void init(FilterConfig config) throws ServletException {
      servletContext = config.getServletContext();
   }

}

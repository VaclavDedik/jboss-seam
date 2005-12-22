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

import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.Manager;

public class SeamServletFilter implements Filter {
   
   private ServletContext servletContext;

   public void destroy() {}

   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
      HttpSession session = ( (HttpServletRequest) request ).getSession(true);
      Lifecycle.setPhaseId(PhaseId.INVOKE_APPLICATION);
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
         throw new ServletException(e);
      }
      finally
      {
         Lifecycle.setPhaseId(null);         
      }
   }

   public void init(FilterConfig config) throws ServletException {
      servletContext = config.getServletContext();
   }

}

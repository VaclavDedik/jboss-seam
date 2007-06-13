package org.jboss.seam.web;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.IOException;

import javax.faces.event.PhaseId;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.jboss.seam.annotations.Filter;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.ConversationPropagation;
import org.jboss.seam.core.Manager;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.servlet.ServletRequestSessionMap;

/**
 * Manages the Seam contexts associated with a request to any servlet.
 * 
 * @author Gavin King
 */
@Startup
@Scope(APPLICATION)
@Name("org.jboss.seam.web.contextFilter")
@Install(value=false, precedence = BUILT_IN)
@Intercept(NEVER)
@Filter(within="org.jboss.seam.web.ajax4jsfFilter")
public class ContextFilter extends AbstractFilter 
{
   private static final LogProvider log = Logging.getLogProvider(ContextFilter.class);
 
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
       throws IOException, ServletException 
   {
      log.debug("beginning request");
      HttpServletRequest hsr = (HttpServletRequest) request;
      Lifecycle.setPhaseId(PhaseId.INVOKE_APPLICATION);
      Lifecycle.setServletRequest(request);
      Lifecycle.beginRequest(getServletContext(), hsr);
      ConversationPropagation.instance().restoreConversationId( request.getParameterMap() );
      Manager.instance().restoreConversation();
      Lifecycle.resumeConversation(hsr);
      Manager.instance().handleConversationPropagation( request.getParameterMap() );
      try
      {
         chain.doFilter(request, response);
         //TODO: conversation timeout
         Manager.instance().endRequest( new ServletRequestSessionMap(hsr)  );
         Lifecycle.endRequest(hsr);
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
}

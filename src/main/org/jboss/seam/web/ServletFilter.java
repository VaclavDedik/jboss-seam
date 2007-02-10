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
import javax.servlet.http.HttpSession;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.contexts.ContextAdaptor;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.Manager;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
/**
 * Manages the Seam contexts associated with a request to any servlet.
 * 
 * @author Gavin King
 */
@Startup
@Scope(APPLICATION)
@Name("org.jboss.seam.servlet.servletFilter")
@Install(precedence = BUILT_IN)
@Intercept(NEVER)
public class ServletFilter extends BaseFilter 
{
   private static final LogProvider log = Logging.getLogProvider(ServletFilter.class);
   private boolean explicitDisabled = false;
   
   /**
    * This filter is disabled by default, unless a urlPattern is set    
    */
   public ServletFilter()
   {
      super.setDisabled(true);
   }
   
   @Override
   public void setUrlPattern(String urlPattern)
   {
      super.setUrlPattern(urlPattern);
      if (!explicitDisabled) setDisabled(false);
   }
   
   @Override
   public void setDisabled(boolean disabled)
   {
      super.setDisabled(disabled);
      if (disabled) explicitDisabled = true;
   }
 
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
       throws IOException, ServletException 
   {
      log.debug("beginning request");
      
      HttpSession session = ( (HttpServletRequest) request ).getSession(true);
      Lifecycle.setPhaseId(PhaseId.INVOKE_APPLICATION);
      Lifecycle.setServletRequest(request);
      Lifecycle.beginRequest(getServletContext(), session, request);
      Manager.instance().restoreConversation( request.getParameterMap() );
      Lifecycle.resumeConversation(session);
      Manager.instance().handleConversationPropagation( request.getParameterMap() );
      try
      {
         chain.doFilter(request, response);
         //TODO: conversation timeout
         Manager.instance().endRequest( ContextAdaptor.getSession(session)  );
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
}
